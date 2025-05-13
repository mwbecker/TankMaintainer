package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.dto.TankParamRequest;
import com.michaelbecker.tankmaintainer.model.ParamType;
import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.model.TankParam;
import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.service.TankParamService;
import com.michaelbecker.tankmaintainer.service.TankService;
import com.michaelbecker.tankmaintainer.service.AppUserService;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tank-params")
public class TankParamController {

    private final TankParamService tankParamService;
    private final TankService tankService;
    private final AppUserService appUserService;

    public TankParamController(TankParamService tankParamService, TankService tankService, AppUserService appUserService) {
        this.tankParamService = tankParamService;
        this.tankService = tankService;
        this.appUserService = appUserService;
    }

    @GetMapping
    public List<TankParam> getAll(@RequestParam(required = false) UUID tankId, HttpServletRequest request) {
        AppUser user = extractUser(request);
        if (tankId != null) {
            tankService.getById(tankId)
                    .filter(t -> t.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new IllegalArgumentException("Tank not found or access denied"));
            return tankParamService.getByTankId(tankId);
        }
        return tankParamService.getAll(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TankParam> getOne(@PathVariable UUID id, HttpServletRequest request) {
        AppUser user = extractUser(request);
        return tankParamService.getById(id)
                .filter(param -> param.getTank().getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    @PostMapping
    public ResponseEntity<TankParam> create(@RequestBody TankParamRequest request, HttpServletRequest httpRequest) {
        AppUser user = extractUser(httpRequest);
        Tank tank = tankService.getById(request.tankId)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Tank not found or access denied"));

        TankParam param = new TankParam();
        param.setTank(tank);
        param.setParamType(ParamType.valueOf(request.paramType));
        param.setValue(request.value);
        param.setUnit(request.unit);
        param.setNotes(request.notes);
        param.setTimestamp(request.timestamp != null ? request.timestamp : OffsetDateTime.now());

        return ResponseEntity.ok(tankParamService.save(param));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, HttpServletRequest request) {
        AppUser user = extractUser(request);
        return tankParamService.getById(id)
                .filter(param -> param.getTank().getUser().getId().equals(user.getId()))
                .map(param -> {
                    tankParamService.delete(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.status(403).build());
    }

    private AppUser extractUser(HttpServletRequest request) {
        FirebaseToken firebaseToken = (FirebaseToken) request.getAttribute("firebaseUser");
        if (firebaseToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return appUserService.getOrCreateUser(
                firebaseToken.getUid(),
                firebaseToken.getEmail(),
                firebaseToken.getName()
        );
    }
}