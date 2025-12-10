package com.techsisters.gatherly.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoogleCalendarUtil {

    private static final String BASE_URL = "https://calendar.google.com/calendar/render?action=TEMPLATE";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    /**
     * Generates a Google Calendar 'Add Event' URL.
     * 
     * @param title         The title of the event.
     * @param description   The details/description of the event (optional, can be
     *                      null).
     * @param location      The location of the event (optional, can be null).
     * @param startDateTime The start time of the event (must be a ZonedDateTime,
     *                      ideally in UTC).
     * @param endDateTime   The end time of the event (must be a ZonedDateTime,
     *                      ideally in UTC).
     * @return A fully encoded Google Calendar URL string.
     */
    public static String generateLink(
            String title,
            String description,
            String location,
            ZonedDateTime startDateTime,
            ZonedDateTime endDateTime) {

        // Ensure date times are in UTC for the 'Z' format required by Google Calendar
        ZonedDateTime utcStart = startDateTime.withZoneSameInstant(java.time.ZoneOffset.UTC);
        ZonedDateTime utcEnd = endDateTime.withZoneSameInstant(java.time.ZoneOffset.UTC);

        // Format dates into the required 'YYYYMMDDThhmmssZ' format
        String dates = utcStart.format(DATE_FORMATTER) + "/" + utcEnd.format(DATE_FORMATTER);

        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL);

            // 1. Add Title (Text)
            urlBuilder.append("&text=").append(urlEncode(title));

            // 2. Add Dates
            urlBuilder.append("&dates=").append(dates);

            // 3. Add Details (Description)
            if (description != null && !description.trim().isEmpty()) {
                urlBuilder.append("&details=").append(urlEncode(description));
            }

            // 4. Add Location
            if (location != null && !location.trim().isEmpty()) {
                urlBuilder.append("&location=").append(urlEncode(location));
            }

            return urlBuilder.toString();

        } catch (UnsupportedEncodingException e) {
            // This should virtually never happen with StandardCharsets.UTF_8
            log.error("Error encoding URL parameter: " + e.getMessage());
            return null;
        }
    }

    /** Helper function to URL-encode strings. */
    private static String urlEncode(String value) throws UnsupportedEncodingException {
        // Use + instead of %20 for spaces, which is common in URL query parameters
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
                .replace("+", "%20");
    }

}
