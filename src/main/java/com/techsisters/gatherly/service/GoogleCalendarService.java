package com.techsisters.gatherly.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.techsisters.gatherly.util.OAuthTokenNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GoogleCalendarService {

    @Autowired
    GoogleAuthService googleAuthService;

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    public String getAccessToken(String userEmail) throws IOException, OAuthTokenNotFoundException {
        return googleAuthService.getValidAccessToken(userEmail);
    }

    private Calendar buildCalendarService(String accessToken) {
        // Create the credential using the user's current Access Token
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("Gatherly").build();
    }

    public Event createEvent(String userEmail, String summary, String description, ZonedDateTime startDateTimeUTC,
            ZonedDateTime endDateTimeUTC) throws IOException, OAuthTokenNotFoundException {

        String accessToken = getAccessToken(userEmail);

        Calendar service = buildCalendarService(accessToken);

        Event event = new Event()
                .setSummary(summary)
                .setDescription(description);

        String timeZoneId = TimeZone.getDefault().getID();

        DateTime start = new DateTime(startDateTimeUTC.toInstant().toEpochMilli());
        EventDateTime startEventDateTime = new EventDateTime()
                .setDateTime(start)
                .setTimeZone(timeZoneId);
        event.setStart(startEventDateTime);

        DateTime end = new DateTime(endDateTimeUTC.toInstant().toEpochMilli());
        EventDateTime endEventDateTime = new EventDateTime()
                .setDateTime(end)
                .setTimeZone(timeZoneId);
        event.setEnd(endEventDateTime);

        // "primary" refers to the user's main calendar
        String calendarId = "primary";

        try {
            // The execute() method sends the request to Google
            Event createdEvent = service.events().insert(calendarId, event).execute();

            log.info("Event created: {} for user {}", createdEvent.getId(), userEmail);
            return createdEvent;
        } catch (GoogleJsonResponseException e) {
            log.error("Error creating event: {}", e);
            throw new IOException("Google Calendar API Error: " + e);
        }
    }

}
