package com.michaelbecker.tankmaintainer.service;

import com.michaelbecker.tankmaintainer.model.TankParam;
import com.michaelbecker.tankmaintainer.repository.TankParamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TankParamService {

    private final TankParamRepository tankParamRepository;

    public TankParamService(TankParamRepository tankParamRepository) {
        this.tankParamRepository = tankParamRepository;
    }

    public List<TankParam> getAll() {
        return tankParamRepository.findAll();
    }

    public Optional<TankParam> getById(UUID id) {
        return tankParamRepository.findById(id);
    }

    public List<TankParam> getByTankId(UUID tankId) {
        return tankParamRepository.findByTankId(tankId);
    }

    public TankParam save(TankParam param) {
        return tankParamRepository.save(param);
    }

    public void delete(UUID id) {
        tankParamRepository.deleteById(id);
    }
}