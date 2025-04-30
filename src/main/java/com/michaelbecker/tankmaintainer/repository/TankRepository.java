package com.michaelbecker.tankmaintainer.repository;

import com.michaelbecker.tankmaintainer.model.Tank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TankRepository extends JpaRepository<Tank, UUID> {
    Optional<Tank> findByName(String name);
}