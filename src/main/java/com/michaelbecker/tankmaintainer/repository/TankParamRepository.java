package com.michaelbecker.tankmaintainer.repository;

import com.michaelbecker.tankmaintainer.model.TankParam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TankParamRepository extends JpaRepository<TankParam, UUID> {
    List<TankParam> findByTankId(UUID tankId);
}