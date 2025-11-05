package com.techsisters.gatherly.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Organizer email is required")
    @Email(message = "Invalid email format")
    @JsonProperty("organizer_email")
    private String organizerEmail;

    @NotBlank(message = "Event datetime is required (ISO format recommended)")
    @JsonProperty("event_datetime")
    private String eventDateTime;

    //@NotBlank(message = "Timezone is required")
    private String timezone;

    @NotBlank(message = "Event type is required (online/in-person)")
    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("event_host_email")
    @Email(message = "Invalid email format")
    private String eventHostEmail;

    @JsonProperty("tags")
    private String tags; // can accept comma-separated list (e.g. "AI,Tech,Community")

    @JsonProperty("duration")
    private Integer duration;

//    @JsonProperty("created_by")
//    private String createdBy;
}
