package com.michaelbecker.tankmaintainer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AutomationConfig {

    private final String token;

    public AutomationConfig(@Value("${automation.token:}") String token) {
        this.token = token;
    }

    public boolean isValidToken(String providedToken) {
        return !token.isBlank() && token.equals(providedToken);
    }
}
