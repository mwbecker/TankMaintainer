package com.michaelbecker.tankmaintainer.repository;

import com.michaelbecker.tankmaintainer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUid(String uid);
}