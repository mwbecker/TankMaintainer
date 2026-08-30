package com.michaelbecker.tankmaintainer.controller;

import com.michaelbecker.tankmaintainer.config.AutomationConfig;
import com.michaelbecker.tankmaintainer.dto.MaintenanceAlertResponse;
import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.repository.AppUserRepository;
import com.michaelbecker.tankmaintainer.service.MaintenanceAlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/automation")
public class AutomationController {

    private final AutomationConfig automationConfig;
    private final AppUserRepository appUserRepository;
    private final MaintenanceAlertService maintenanceAlertService;

    public AutomationController(
            AutomationConfig automationConfig,
            AppUserRepository appUserRepository,
            MaintenanceAlertService maintenanceAlertService
    ) {
        this.automationConfig = automationConfig;
        this.appUserRepository = appUserRepository;
        this.maintenanceAlertService = maintenanceAlertService;
    }

    @GetMapping("/overdue-maintenance")
    public ResponseEntity<List<MaintenanceAlertResponse>> getOverdueMaintenance(
            @RequestParam String email,
            @RequestHeader("X-Automation-Token") String token
    ) {
        if (!automationConfig.isValidToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid automation token");
        }

        AppUser user = appUserRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(maintenanceAlertService.getOverdueAlertsForUser(user));
    }
}
