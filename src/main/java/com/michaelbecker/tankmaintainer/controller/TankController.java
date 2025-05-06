package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.model.Tank;
import com.google.firebase.auth.FirebaseToken;
import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.service.AppUserService;
import com.michaelbecker.tankmaintainer.service.TankService;
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
    private final AppUserService appUserService;

    public TankController(TankService tankService, AppUserService appUserService) {
        this.tankService = tankService;
        this.appUserService = appUserService;
    }

    @GetMapping
    public List<Tank> getAll(HttpServletRequest request) {
        AppUser user = extractUser(request);
        return tankService.getAllByUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tank> getOne(@PathVariable UUID id, HttpServletRequest request) {
        AppUser user = extractUser(request);
        return tankService.getById(id)
                .filter(existing -> existing.getUser().getId().equals(user.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());
    }

    @PostMapping
    public Tank create(@RequestBody Tank tank, HttpServletRequest request) {
        AppUser user = extractUser(request);
        tank.setUser(user);
        return tankService.save(tank);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tank> update(@PathVariable UUID id, @RequestBody Tank updatedTank, HttpServletRequest request) {
        AppUser user = extractUser(request);
        return tankService.getById(id)
                .filter(existing -> existing.getUser().getId().equals(user.getId()))
                .map(existing -> {
                    updatedTank.setId(existing.getId());
                    updatedTank.setUser(existing.getUser());
                    return ResponseEntity.ok(tankService.save(updatedTank));
                })
                .orElse(ResponseEntity.status(403).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, HttpServletRequest request) {
        AppUser user = extractUser(request);

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

    // Updated helper method
    private AppUser extractUser(HttpServletRequest request) {
        FirebaseToken firebaseToken = (FirebaseToken) request.getAttribute("firebaseUser");
        if (firebaseToken == null) {
            throw new IllegalStateException("Missing Firebase token from request.");
        }

        return appUserService.getOrCreateUser(
                firebaseToken.getUid(),
                firebaseToken.getEmail(),
                firebaseToken.getName()
        );
    }
}