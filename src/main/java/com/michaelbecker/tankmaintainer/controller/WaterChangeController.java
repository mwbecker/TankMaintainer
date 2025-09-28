package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.dto.NextMaintenanceResponse;
import com.michaelbecker.tankmaintainer.dto.WaterChangeRequest;
import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.model.WaterChange;
import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.repository.TankRepository;
import com.michaelbecker.tankmaintainer.service.WaterChangeService;
import com.michaelbecker.tankmaintainer.util.SecurityUtils;
import java.time.temporal.ChronoUnit;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/water-changes")
public class WaterChangeController {

    private final WaterChangeService service;
    private final TankRepository tankRepository;
    private final SecurityUtils securityUtils;

    public WaterChangeController(WaterChangeService service, TankRepository tankRepository, SecurityUtils securityUtils) {
        this.service = service;
        this.tankRepository = tankRepository;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public List<WaterChange> getAll(@RequestParam(required = false) UUID tankId, HttpServletRequest request) {
        AppUser user = securityUtils.extractUser(request);
        if (tankId != null) {
            tankRepository.findById(tankId)
                    .filter(t -> t.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new IllegalArgumentException("Tank not found or access denied"));
            return service.getByTankId(tankId);
        }
        return service.getAll(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaterChange> getOne(@PathVariable UUID id, HttpServletRequest request) {
        AppUser user = securityUtils.extractUser(request);
        return service.getById(id)
                .filter(change -> change.getTank().getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    @PostMapping
    public ResponseEntity<WaterChange> create(@RequestBody WaterChangeRequest request, HttpServletRequest httpRequest) {
        AppUser user = securityUtils.extractUser(httpRequest);
        Tank tank = tankRepository.findById(request.getTankId())
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Tank not found or access denied"));

        WaterChange change = new WaterChange();
        change.setTank(tank);
        change.setDate(request.getDate());
        change.setVolumeGallons(request.getVolumeGallons());
        change.setNotes(request.getNotes());

        WaterChange saved = service.save(change);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, HttpServletRequest request) {
        AppUser user = securityUtils.extractUser(request);
        return service.getById(id)
                .filter(change -> change.getTank().getUser().getId().equals(user.getId()))
                .map(change -> {
                    service.delete(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.status(403).build());
    }

@GetMapping("/predict-next/{tankId}")
public ResponseEntity<NextMaintenanceResponse> predictNext(
        @PathVariable UUID tankId,
        HttpServletRequest request
) {
    AppUser user = securityUtils.extractUser(request);

    Tank tank = tankRepository.findById(tankId)
            .filter(t -> t.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new IllegalArgumentException("Tank not found or access denied"));
    
    if (!tank.getUser().getId().equals(user.getId())) {
        return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
    }
    LocalDate nextDate = service.predictNextMaintenance(tankId);
    long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), nextDate);

    return ResponseEntity.ok(new NextMaintenanceResponse(
        tankId,
        nextDate,
        daysRemaining
    ));
}
}