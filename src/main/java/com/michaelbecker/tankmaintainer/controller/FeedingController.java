package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.model.Feeding;
import com.michaelbecker.tankmaintainer.service.FeedingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feedings")
public class FeedingController {

    private final FeedingService service;

    public FeedingController(FeedingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Feeding> getAll(@RequestParam(required = false) UUID tankId) {
        return (tankId != null)
                ? service.getByTankId(tankId)
                : service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feeding> getOne(@PathVariable UUID id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Feeding create(@RequestBody Feeding feeding) {
        return service.save(feeding);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}