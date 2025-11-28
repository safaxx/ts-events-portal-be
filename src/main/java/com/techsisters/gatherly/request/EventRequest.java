package com.techsisters.gatherly.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class EventRequest {


    @NotBlank(message = "Title is required")
    @Length(max = 120, message = "Title cannot exceed 120 characters")
    private String title;

    @NotBlank(message = "Short description is required")
    @JsonProperty("short_description")
    @Length(max = 200, message = "Short description cannot exceed 200 characters")
    private String shortDescription;

    @JsonProperty("long_description")
    //@Length(max = 2000, message = "Long description cannot exceed 2000 characters")
    private String longDescription;


    @Email(message = "Invalid email format")
    @JsonProperty("organizer_email")
    private String organizerEmail;


    @Email(message = "Invalid host email format")
    @JsonProperty("event_host_email")
    private String eventHostEmail;


    @NotBlank(message = "Host name is required")
    @JsonProperty("event_host_name")
    @Length(max = 100, message = "Host name cannot exceed 100 characters")
    private String eventHostName;

    private String createdBy; // same as organizerEmail

    @NotBlank(message = "Event datetime is required")
    @JsonProperty("event_datetime")
    private String eventDateTime;

    private String timezone;

    @NotBlank(message = "Event type is required (online/in-person)")
    @Pattern(
            regexp = "online|in-person",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Event type must be 'online' or 'in-person'"
    )
    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("event_link")
    private String eventLink; // required when online

    @JsonProperty("event_location")
    private String eventLocation; // required when in-person


    @NotNull(message = "Duration is required")
    @Min(value = 10, message = "Duration must be at least 10 minute")
    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("tags")
    @Pattern(
            regexp = "^[a-z ,]*$",
            message = "Tags may contain only letters, commas and spaces"
    )
    @Length(max = 200, message = "Tags cannot exceed 200 characters")
    private String tags;


//    @AssertTrue(message = "Event link is required for online events")
//    public boolean isOnlineEventValid() {
//        if ("online".equalsIgnoreCase(eventType)) {
//            return eventLink != null && !eventLink.trim().isEmpty();
//        }
//        return true;
//    }
//
//    @AssertTrue(message = "Event location is required for in-person events")
//    public boolean isInPersonEventValid() {
//        if ("in-person".equalsIgnoreCase(eventType)) {
//            return eventLocation != null && !eventLocation.trim().isEmpty();
//        }
//        return true; // ignore for online
//    }
}
