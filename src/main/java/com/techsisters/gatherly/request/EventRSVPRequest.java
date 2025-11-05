package com.techsisters.gatherly.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRSVPRequest {

    @NotNull(message = "Event ID is required")
    @JsonProperty("event_id")
    private Long eventId;

    @Email(message = "Invalid email format")
    @NotBlank(message = "User email is required")
    @JsonProperty("user_email")
    private String userEmail;

    private Boolean rsvp = true; // default true
}
