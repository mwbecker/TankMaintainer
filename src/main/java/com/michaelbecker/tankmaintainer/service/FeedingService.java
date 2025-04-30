package com.michaelbecker.tankmaintainer.service;

import com.michaelbecker.tankmaintainer.model.Feeding;
import com.michaelbecker.tankmaintainer.repository.FeedingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FeedingService {

    private final FeedingRepository feedingRepository;

    public FeedingService(FeedingRepository feedingRepository) {
        this.feedingRepository = feedingRepository;
    }

    public List<Feeding> getAll() {
        return feedingRepository.findAll();
    }

    public Optional<Feeding> getById(UUID id) {
        return feedingRepository.findById(id);
    }

    public List<Feeding> getByTankId(UUID tankId) {
        return feedingRepository.findByTankId(tankId);
    }

    public Feeding save(Feeding feeding) {
        return feedingRepository.save(feeding);
    }

    public void delete(UUID id) {
        feedingRepository.deleteById(id);
    }
}