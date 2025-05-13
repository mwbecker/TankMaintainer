package com.michaelbecker.tankmaintainer.repository;

import com.michaelbecker.tankmaintainer.model.WaterChange;
import com.michaelbecker.tankmaintainer.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WaterChangeRepository extends JpaRepository<WaterChange, UUID> {
    List<WaterChange> findByTankId(UUID tankId);
    
    @Query("SELECT w FROM WaterChange w WHERE w.tank.user = :user")
    List<WaterChange> findByUser(@Param("user") AppUser user);
}