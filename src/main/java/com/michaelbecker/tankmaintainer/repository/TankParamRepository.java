package com.michaelbecker.tankmaintainer.repository;

import com.michaelbecker.tankmaintainer.model.TankParam;
import com.michaelbecker.tankmaintainer.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TankParamRepository extends JpaRepository<TankParam, UUID> {
    List<TankParam> findByTankId(UUID tankId);
    
    @Query("SELECT p FROM TankParam p WHERE p.tank.user = :user")
    List<TankParam> findByUser(@Param("user") AppUser user);
}