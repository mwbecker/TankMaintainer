package com.michaelbecker.tankmaintainer.service;

import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.repository.TankRepository;
import org.springframework.stereotype.Service;
import com.michaelbecker.tankmaintainer.model.AppUser;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.util.StringUtils;

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

    public List<Tank> getAllByUser(AppUser user) {
        return tankRepository.findByUser(user);
    }

    public List<Tank> listActiveByUser(AppUser user, String q) {
        if (StringUtils.hasText(q)) {
            return tankRepository.findByUserAndArchivedAndNameContainingIgnoreCase(user, false, q.trim());
        }
        return tankRepository.findByUserAndArchived(user, false);
    }

    public List<Tank> listArchivedByUser(AppUser user, String q) {
        if (StringUtils.hasText(q)) {
            return tankRepository.findByUserAndArchivedAndNameContainingIgnoreCase(user, true, q.trim());
        }
        return tankRepository.findByUserAndArchived(user, true);
    }

    public Optional<Tank> archive(UUID id, AppUser user) {
        return tankRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .map(t -> {
                    t.setArchived(true);
                    return tankRepository.save(t);
                });
    }

    public Optional<Tank> reactivate(UUID id, AppUser user) {
        return tankRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .map(t -> {
                    t.setArchived(false);
                    return tankRepository.save(t);
                });
    }
}