package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.service.TankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tanks")
public class TankController {

    private final TankService tankService;

    public TankController(TankService tankService) {
        this.tankService = tankService;
    }

    @GetMapping
    public List<Tank> getAll() {
        return tankService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tank> getOne(@PathVariable UUID id) {
        return tankService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Tank create(@RequestBody Tank tank) {
        return tankService.save(tank);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tank> update(@PathVariable UUID id, @RequestBody Tank updatedTank) {
        return tankService.getById(id)
                .map(existing -> {
                    updatedTank.setId(existing.getId());
                    return ResponseEntity.ok(tankService.save(updatedTank));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tankService.delete(id);
        return ResponseEntity.noContent().build();
    }
}