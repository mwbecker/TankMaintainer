package com.michaelbecker.tankmaintainer.service;

import com.michaelbecker.tankmaintainer.model.User;
import com.michaelbecker.tankmaintainer.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    public User getOrCreateUser(String uid, String email, String displayName) {
        return userRepository.findByUid(uid).orElseGet(() -> {
            User newUser = new User();
            newUser.setUid(uid);
            newUser.setEmail(email);
            newUser.setDisplayName(displayName);
            return userRepository.save(newUser);
        });
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}