package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.model.WaterChange;
import com.michaelbecker.tankmaintainer.service.WaterChangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/water-changes")
public class WaterChangeController {

    private final WaterChangeService service;

    public WaterChangeController(WaterChangeService service) {
        this.service = service;
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
    public WaterChange create(@RequestBody WaterChange change) {
        return service.save(change);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}