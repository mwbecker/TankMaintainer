package com.michaelbecker.tankmaintainer.service;

import com.michaelbecker.tankmaintainer.model.WaterChange;
import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.repository.WaterChangeRepository;
import org.springframework.stereotype.Service;

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
}