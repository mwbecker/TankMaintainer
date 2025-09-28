package com.michaelbecker.tankmaintainer.util;

import com.google.firebase.auth.FirebaseToken;
import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SecurityUtils {
    
    private final AppUserService appUserService;

    public SecurityUtils(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    public AppUser extractUser(HttpServletRequest request) {
        FirebaseToken firebaseToken = (FirebaseToken) request.getAttribute("firebaseUser");
        if (firebaseToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return appUserService.getOrCreateUser(
                firebaseToken.getUid(),
                firebaseToken.getEmail(),
                firebaseToken.getName()
        );
    }
} 