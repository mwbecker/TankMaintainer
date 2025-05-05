package com.michaelbecker.tankmaintainer.repository;

import com.michaelbecker.tankmaintainer.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByUid(String uid);
}