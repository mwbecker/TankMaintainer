package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.model.AppUser;
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

    public TankController(TankService tankService) {
        this.tankService = tankService;
    }

    @GetMapping
    public List<Tank> getAll(HttpServletRequest request) {
        AppUser user = extractUser(request);
        return tankService.getAllByUser(user);  // Fetch tanks only for the current user
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tank> getOne(@PathVariable UUID id, HttpServletRequest request) {
        AppUser user = extractUser(request);
        return tankService.getById(id)
                .filter(existing -> existing.getUser().getId().equals(user.getId()))  // Ensure the tank belongs to the user
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build());  // Forbidden if not owned by the user
    }

    @PostMapping
    public Tank create(@RequestBody Tank tank, HttpServletRequest request) {
        AppUser user = extractUser(request);
        tank.setUser(user);  // Set the current user for the new tank
        return tankService.save(tank);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tank> update(@PathVariable UUID id, @RequestBody Tank updatedTank, HttpServletRequest request) {
        AppUser user = extractUser(request);
        return tankService.getById(id)
                .filter(existing -> existing.getUser().getId().equals(user.getId()))  // Check if the tank belongs to the user
                .map(existing -> {
                    updatedTank.setId(existing.getId());  // Update with the existing tank's ID
                    updatedTank.setUser(existing.getUser());  // Retain the user information
                    return ResponseEntity.ok(tankService.save(updatedTank));
                })
                .orElse(ResponseEntity.status(403).build());  // Forbidden if the user does not own the tank
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, HttpServletRequest request) {
    AppUser user = extractUser(request);

    // Fetch the tank, check if it exists, and if the user matches.
    Optional<Tank> optionalTank = tankService.getById(id);
    if (optionalTank.isPresent()) {
        Tank existingTank = optionalTank.get();
        
        // Check if the tank belongs to the current user
        if (existingTank.getUser().getId().equals(user.getId())) {
            // Delete the tank
            tankService.delete(id);
            return ResponseEntity.noContent().build(); // 204 No Content response
        } else {
            // User is not authorized to delete this tank
            return ResponseEntity.status(403).build(); // 403 Forbidden response
        }
    } else {
        // Tank not found
        return ResponseEntity.notFound().build(); // 404 Not Found response
    }
    }

    // Helper method to extract the current user from the request
    private AppUser extractUser(HttpServletRequest request) {
        return (AppUser) request.getAttribute("firebaseUser");  // Extract the user info from the request attribute
    }
}