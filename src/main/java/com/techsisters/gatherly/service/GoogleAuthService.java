package com.techsisters.gatherly.service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.techsisters.gatherly.entity.User;
import com.techsisters.gatherly.util.OAuthTokenNotFoundException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

        @Value("${google.oauth.clientId}")
        private String clientId;

        @Value("${google.oauth.clientSecret}")
        private String clientSecret;

        // Must match the URI registered in Google Console
        @Value("${google.oauth.redirectUri}")
        private String redirectUri;

        @Autowired
        UserService userService;

        private GoogleAuthorizationCodeFlow flow;
        private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        private static final Collection<String> SCOPES = Collections
                        .singletonList("https://www.googleapis.com/auth/calendar.events");

        @PostConstruct
        public GoogleAuthorizationCodeFlow init() throws IOException {

                this.flow = new GoogleAuthorizationCodeFlow.Builder(
                                new NetHttpTransport(), // HTTP Transport layer
                                GsonFactory.getDefaultInstance(), // JSON parser
                                clientId,
                                clientSecret,
                                SCOPES)
                                .setAccessType("offline") // Required to get a Refresh Token
                                .build();
                return flow;
        }

        public static String generateState() {
                return UUID.randomUUID().toString();
        }

        public void setUserState(String email) {
                User user = userService.findByEmail(email);
                if (user != null) {
                        user.setState(generateState());
                        userService.saveUser(user);
                }

        }

        public String buildAuthorizationUrl(String state) {
                String scope = "https://www.googleapis.com/auth/calendar.events";

                // Request "offline" access to receive a Refresh Token
                String accessType = "offline";

                return "https://accounts.google.com/o/oauth2/v2/auth?" +
                                "client_id=" + clientId + "&" +
                                "redirect_uri=" + redirectUri + "&" +
                                "response_type=code&" +
                                "scope=" + scope + "&" +
                                "access_type=" + accessType + "&" +
                                // The 'state' parameter links the Google response back to your logged-in user
                                "state=" + state;
        }

        public void exchangeCodeForTokens(String userEmail, String authorizationCode) throws IOException {

                GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                                new NetHttpTransport(),
                                JSON_FACTORY,
                                clientId,
                                clientSecret,
                                List.of("https://www.googleapis.com/auth/calendar.events"))
                                .setAccessType("offline")
                                .build();

                // 1. Build the token request
                TokenResponse tokenResponse = flow.newTokenRequest(authorizationCode)
                                .setRedirectUri(redirectUri)
                                .execute();

                // 2. Save the tokens
                saveTokens(userEmail, tokenResponse);
        }

        private void saveTokens(String userEmail, TokenResponse tokenResponse) {
                User user = userService.findByEmail(userEmail);
                if (user == null) {
                        throw new IllegalArgumentException("User with email " + userEmail + " not found");
                }

                // Encrypt and save the Refresh Token
                // The Refresh Token is essential for long-term access
                if (tokenResponse.getRefreshToken() != null) {

                        user.setGoogleRefreshToken(tokenResponse.getRefreshToken());
                }
                log.debug("Tokens saved for user {}", userEmail);

                // Save the first Access Token
                user.setGoogleAccessToken(tokenResponse.getAccessToken());
                user.setGoogleAccessTokenExpiry(
                                Date.from(Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds())));

                userService.saveUser(user);
        }

        public String getValidAccessToken(String userEmail) throws IOException, OAuthTokenNotFoundException {

                User user = userService.findByEmail(userEmail);
                if (user == null) {
                        throw new IllegalArgumentException("User with email " + userEmail + " not found");
                }

                if (user.getGoogleAccessToken() == null) {
                        throw new OAuthTokenNotFoundException("Google OAuth token not found");
                }

                // Define a small buffer time (e.g., 5 minutes) to initiate refresh early
                Instant bufferTime = Instant.now().plus(Duration.ofMinutes(5));

                if (user.getGoogleAccessTokenExpiry() != null
                                && user.getGoogleAccessTokenExpiry().toInstant().isAfter(bufferTime)) {

                        // Token is still valid: Return the stored token
                        return user.getGoogleAccessToken();
                }

                // Token is expired or expiring soon, call the refresh method
                return refreshAccessToken(user.getGoogleRefreshToken(), user);
        }

        // Refreshes the Access Token using the stored Refresh Token
        public String refreshAccessToken(String storedRefreshToken, User user)
                        throws IOException, OAuthTokenNotFoundException {
                log.info("Refreshing access token for user {}", user.getEmail());
                GoogleCredential credential = new GoogleCredential.Builder()
                                .setTransport(new NetHttpTransport())
                                .setJsonFactory(JSON_FACTORY)
                                .setClientSecrets(clientId, clientSecret)
                                .build();

                credential.setRefreshToken(storedRefreshToken);

                boolean success = credential.refreshToken();
                if (success) {
                        user.setGoogleAccessToken(credential.getAccessToken());
                        user.setGoogleAccessTokenExpiry(
                                        Date.from(Instant.now().plusSeconds(credential.getExpiresInSeconds())));

                        String newRefreshToken = credential.getRefreshToken();

                        if (StringUtils.isNotBlank(newRefreshToken)) {
                                user.setGoogleRefreshToken(newRefreshToken);
                        }
                        userService.saveUser(user);
                        log.info("Access token refreshed for user {}", user.getEmail());

                        return credential.getAccessToken();
                } else {
                        log.info("No refresh token found for user {}", user.getEmail());
                        throw new OAuthTokenNotFoundException("Google OAuth token not found");
                }

        }

}
