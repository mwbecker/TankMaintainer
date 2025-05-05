package com.michaelbecker.tankmaintainer.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.service.AppUserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FirebaseAuthenticationFilter implements Filter {

    private final AppUserService userService;

    public FirebaseAuthenticationFilter(AppUserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String idToken = authHeader.substring(7);
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

                // Call getOrCreateUser
                AppUser appUser = userService.getOrCreateUser(
                    decodedToken.getUid(),
                    decodedToken.getEmail(),
                    decodedToken.getName()
                );

                request.setAttribute("firebaseUser", decodedToken);
                request.setAttribute("appUser", appUser); // Optionally attach to request
            } catch (Exception e) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase token");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}