package com.michaelbecker.tankmaintainer.service;

import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<AppUser> getByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    public AppUser getOrCreateUser(String uid, String email, String displayName) {
        return userRepository.findByUid(uid).orElseGet(() -> {
            AppUser newUser = new AppUser();
            newUser.setUid(uid);
            newUser.setEmail(email);
            newUser.setDisplayName(displayName);
            return userRepository.save(newUser);
        });
    }

    public AppUser save(AppUser user) {
        return userRepository.save(user);
    }
}