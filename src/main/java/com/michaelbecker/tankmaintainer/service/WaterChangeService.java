package com.michaelbecker.tankmaintainer.service;

import com.michaelbecker.tankmaintainer.model.WaterChange;
import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.repository.WaterChangeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WaterChangeService {

    private final WaterChangeRepository waterChangeRepository;

    public WaterChangeService(WaterChangeRepository waterChangeRepository) {
        this.waterChangeRepository = waterChangeRepository;
    }

    public List<WaterChange> getAll(AppUser user) {
        return waterChangeRepository.findByUser(user);
    }

    public Optional<WaterChange> getById(UUID id) {
        return waterChangeRepository.findById(id);
    }

    public List<WaterChange> getByTankId(UUID tankId) {
        return waterChangeRepository.findByTankId(tankId);
    }

    public WaterChange save(WaterChange change) {
        return waterChangeRepository.save(change);
    }

    public void delete(UUID id) {
        waterChangeRepository.deleteById(id);
    }

    public LocalDate predictNextMaintenance(UUID tankId) {
        List<WaterChange> changes = waterChangeRepository.findByTankId(tankId);

        if (changes.size() < 2) {
            // Fallback: not enough history, default to weekly
            return LocalDate.now().plusDays(7);
        }

        // Sort by date ascending
        changes.sort(Comparator.comparing(w -> w.getDate()));

        // Calculate average interval
        long totalDays = 0;
        for (int i = 1; i < changes.size(); i++) {
            long diff = ChronoUnit.DAYS.between(
                changes.get(i - 1).getDate().toLocalDate(),
                changes.get(i).getDate().toLocalDate()
            );
            totalDays += diff;
        }

        long avgInterval = Math.round((double) totalDays / (changes.size() - 1));

        LocalDate lastChange = changes.get(changes.size() - 1).getDate().toLocalDate();

        return lastChange.plusDays(avgInterval);
    }
}