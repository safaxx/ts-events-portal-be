package com.techsisters.gatherly.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techsisters.gatherly.entity.User;
import com.techsisters.gatherly.service.GoogleAuthService;
import com.techsisters.gatherly.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GoogleCallbackController {

    @Autowired
    GoogleAuthService authService;

    @Autowired
    public UserService userService;

    @GetMapping("/oauth2callback") // Matches the redirect-uri config
    public ResponseEntity<String> handleGoogleCallback(
            @RequestParam("code") String authorizationCode,
            @RequestParam("state") String state,
            @RequestParam(value = "error", required = false) String error) {
        log.info("Google callback called: {}", state);

        String[] parts = state.split("\\|", 2);
        String secureToken = parts[0];
        String finalReturnUrl = parts.length > 1 ? parts[1] : "localhost:3000/dashboard";

        // Use secureToken to fetch the user from DB
        User user = userService.findByState(secureToken);

        if (user == null) {
            // Handle error, e.g., user denied access
            return ResponseEntity.badRequest().body("Google authorization failed: ");
        }

        try {
            log.info("Google authorization successful for user: {}", user.getEmail());
            // Exchange the code for tokens and save them.
            authService.exchangeCodeForTokens(user.getEmail(), authorizationCode);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", finalReturnUrl)
                    .build();
        } catch (IOException e) {
            log.error("Failed to exchange code for tokens.", e);
            // Handle network/API errors
            return ResponseEntity.internalServerError().body("Failed to exchange code for tokens.");
        }
    }

}
