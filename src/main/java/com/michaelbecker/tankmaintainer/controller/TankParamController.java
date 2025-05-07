package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.dto.TankParamRequest;
import com.michaelbecker.tankmaintainer.model.ParamType;
import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.model.TankParam;
import com.michaelbecker.tankmaintainer.service.TankParamService;
import com.michaelbecker.tankmaintainer.service.TankService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tank-params")
public class TankParamController {

    private final TankParamService tankParamService;
    private final TankService tankService; 


    public TankParamController(TankParamService tankParamService, TankService tankService) {
        this.tankParamService = tankParamService;
        this.tankService = tankService;
    }

    @GetMapping
    public List<TankParam> getAll(@RequestParam(required = false) UUID tankId) {
        return (tankId != null)
                ? tankParamService.getByTankId(tankId)
                : tankParamService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TankParam> getOne(@PathVariable UUID id) {
        return tankParamService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TankParam create(@RequestBody TankParamRequest request) {
        Tank tank = tankService.getById(request.tankId)
                .orElseThrow(() -> new IllegalArgumentException("Tank not found"));

        TankParam param = new TankParam();
        param.setTank(tank);
        param.setParamType(ParamType.valueOf(request.paramType));
        param.setValue(request.value);
        param.setUnit(request.unit);
        param.setNotes(request.notes);
        param.setTimestamp(request.timestamp != null ? request.timestamp : LocalDateTime.now());

        return tankParamService.save(param);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tankParamService.delete(id);
        return ResponseEntity.noContent().build();
    }
}