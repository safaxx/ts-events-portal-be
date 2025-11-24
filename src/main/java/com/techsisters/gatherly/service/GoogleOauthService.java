package com.techsisters.gatherly.service;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class GoogleOauthService {

    @Value("${google.oauth.clientId}")
    private String clientId;

    @Value("${google.oauth.clientSecret}")
    private String clientSecret;

    private static final java.util.Collection<String> SCOPES = Collections
            .singletonList("https://www.googleapis.com/auth/calendar.events");

    private final GoogleAuthorizationCodeFlow flow;

    public GoogleOauthService() throws IOException {
        flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), // HTTP Transport layer
                GsonFactory.getDefaultInstance(), // JSON parser
                clientId,
                clientSecret,
                SCOPES)
                .build();
    }

    public GoogleTokenResponse exchangeCodeForTokens(String authorizationCode) throws IOException {
        GoogleTokenResponse response = flow.newTokenRequest(authorizationCode)
                .execute();

        return response;
    }

}
