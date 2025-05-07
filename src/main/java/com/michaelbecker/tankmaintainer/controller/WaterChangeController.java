package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.dto.WaterChangeRequest;
import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.model.WaterChange;
import com.michaelbecker.tankmaintainer.repository.TankRepository;
import com.michaelbecker.tankmaintainer.service.WaterChangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/water-changes")
public class WaterChangeController {

    private final WaterChangeService service;
    private final TankRepository tankRepository;

    public WaterChangeController(WaterChangeService service, TankRepository tankRepository) {
        this.service = service;
        this.tankRepository = tankRepository;
    }

    @GetMapping
    public List<WaterChange> getAll(@RequestParam(required = false) UUID tankId) {
        return (tankId != null)
                ? service.getByTankId(tankId)
                : service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaterChange> getOne(@PathVariable UUID id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<WaterChange> create(@RequestBody WaterChangeRequest request) {
        Tank tank = tankRepository.findById(request.getTankId())
                .orElseThrow(() -> new IllegalArgumentException("Tank not found with id: " + request.getTankId()));

        WaterChange change = new WaterChange();
        change.setTank(tank);
        change.setDate(request.getDate());
        change.setVolumeGallons(request.getVolumeGallons());
        change.setNotes(request.getNotes());

        WaterChange saved = service.save(change);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}