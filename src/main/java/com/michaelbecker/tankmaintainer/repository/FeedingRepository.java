package com.michaelbecker.tankmaintainer.repository;

import com.michaelbecker.tankmaintainer.model.Feeding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedingRepository extends JpaRepository<Feeding, UUID> {
    List<Feeding> findByTankId(UUID tankId);
}