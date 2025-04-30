package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.model.TankParam;
import com.michaelbecker.tankmaintainer.service.TankParamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tank-params")
public class TankParamController {

    private final TankParamService tankParamService;

    public TankParamController(TankParamService tankParamService) {
        this.tankParamService = tankParamService;
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
    public TankParam create(@RequestBody TankParam param) {
        return tankParamService.save(param);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tankParamService.delete(id);
        return ResponseEntity.noContent().build();
    }
}