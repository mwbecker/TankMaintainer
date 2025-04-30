package com.michaelbecker.tankmaintainer.service;

import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.repository.TankRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TankService {

    private final TankRepository tankRepository;

    public TankService(TankRepository tankRepository) {
        this.tankRepository = tankRepository;
    }

    public List<Tank> getAll() {
        return tankRepository.findAll();
    }

    public Optional<Tank> getById(UUID id) {
        return tankRepository.findById(id);
    }

    public Tank save(Tank tank) {
        return tankRepository.save(tank);
    }

    public void delete(UUID id) {
        tankRepository.deleteById(id);
    }
}