package com.michaelbecker.tankmaintainer.service;

import com.michaelbecker.tankmaintainer.dto.MaintenanceAlertResponse;
import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.repository.TankRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceAlertService {

    private final TankRepository tankRepository;
    private final WaterChangeService waterChangeService;

    public MaintenanceAlertService(TankRepository tankRepository, WaterChangeService waterChangeService) {
        this.tankRepository = tankRepository;
        this.waterChangeService = waterChangeService;
    }

    public List<MaintenanceAlertResponse> getOverdueAlertsForUser(AppUser user) {
        return tankRepository.findByUserAndArchived(user, false).stream()
                .map(this::toAlertIfOverdue)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<MaintenanceAlertResponse> toAlertIfOverdue(Tank tank) {
        LocalDate nextDate = waterChangeService.predictNextMaintenance(tank.getId());
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), nextDate);
        if (daysRemaining >= 0) {
            return Optional.empty();
        }

        LocalDate lastChangeDate = waterChangeService.getLastChangeDate(tank.getId()).orElse(null);

        return Optional.of(new MaintenanceAlertResponse(
                tank.getId(),
                tank.getName(),
                lastChangeDate,
                nextDate,
                daysRemaining,
                tank.getVolumeGallons(),
                tank.getSpecies()
        ));
    }
}
