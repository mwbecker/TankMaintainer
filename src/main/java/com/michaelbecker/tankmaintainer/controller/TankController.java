package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.service.TankService;
import com.michaelbecker.tankmaintainer.util.SecurityUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tanks")
public class TankController {

    private final TankService tankService;
    private final SecurityUtils securityUtils;

    public TankController(TankService tankService, SecurityUtils securityUtils) {
        this.tankService = tankService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public List<Tank> getAll(
            @RequestParam(required = false) String q,
            HttpServletRequest request) {
        AppUser user = securityUtils.extractUser(request);
        return tankService.listActiveByUser(user, q);
    }

    @GetMapping("/archived")
    public List<Tank> getArchived(
            @RequestParam(required = false) String q,
            HttpServletRequest request) {
        AppUser user = securityUtils.extractUser(request);
        return tankService.listArchivedByUser(user, q);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tank> getOne(@PathVariable UUID id, HttpServletRequest request) {
        AppUser user = securityUtils.extractUser(request);
        return tankService.getById(id)
                .filter(existing -> existing.getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    @PostMapping
    public Tank create(@RequestBody Tank tank, HttpServletRequest request) {
        AppUser user = securityUtils.extractUser(request);
        tank.setUser(user);
        tank.setArchived(false);
        return tankService.save(tank);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tank> update(@PathVariable UUID id, @RequestBody Tank updatedTank, HttpServletRequest request) {
        AppUser user = securityUtils.extractUser(request);
        return tankService.getById(id)
                .filter(existing -> existing.getUser().getId().equals(user.getId()))
                .map(existing -> {
                    updatedTank.setId(existing.getId());
                    updatedTank.setUser(existing.getUser());
                    updatedTank.setArchived(existing.isArchived());
                    return ResponseEntity.ok(tankService.save(updatedTank));
                })
                .orElse(ResponseEntity.status(403).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, HttpServletRequest request) {
        AppUser user = securityUtils.extractUser(request);

        Optional<Tank> optionalTank = tankService.getById(id);
        if (optionalTank.isPresent()) {
            Tank existingTank = optionalTank.get();
            if (existingTank.getUser().getId().equals(user.getId())) {
                tankService.delete(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(403).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Tank> archive(@PathVariable UUID id, HttpServletRequest request) {
        AppUser user = securityUtils.extractUser(request);
        return tankService.archive(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/reactivate")
    public ResponseEntity<Tank> reactivate(@PathVariable UUID id, HttpServletRequest request) {
        AppUser user = securityUtils.extractUser(request);
        return tankService.reactivate(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}