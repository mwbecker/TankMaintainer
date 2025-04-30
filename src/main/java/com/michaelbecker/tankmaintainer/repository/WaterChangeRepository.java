package com.michaelbecker.tankmaintainer.repository;

import com.michaelbecker.tankmaintainer.model.WaterChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WaterChangeRepository extends JpaRepository<WaterChange, UUID> {
    List<WaterChange> findByTankId(UUID tankId);
}