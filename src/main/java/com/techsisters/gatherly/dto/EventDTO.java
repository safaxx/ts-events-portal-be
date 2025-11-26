package com.techsisters.gatherly.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
public class EventDTO {
    private Long eventId;
    private String title;
    private String shortDescription;
    private String longDescription;
    private OffsetDateTime eventDateTime;
    private LocalDateTime createdDate = LocalDateTime.now();
    private String timezone;
    private String organizerEmail;
    private String tags;
    private Integer duration;
    private String eventType;
    private String eventHostEmail;
    private Integer allRSVPs;
    private String createdBy;
    private Boolean currentUserRSVP;
    private String eventLink;
    private String eventLocation;



}
