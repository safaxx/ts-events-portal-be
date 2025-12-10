package com.techsisters.gatherly.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import com.techsisters.gatherly.util.GoogleCalendarUtil;

import lombok.Getter;
import lombok.Setter;

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
    private String eventHostName;
    private Integer allRSVPs;
    private String createdBy;
    private Boolean currentUserRSVP;
    private String eventLink;
    private String eventLocation;

    private String googleCalendarLink;

    public String getGoogleCalendarLink() {
        String link = googleCalendarLink;
        if (link == null) {
            ZonedDateTime startDateTimeUTC = eventDateTime.toZonedDateTime();
            ZonedDateTime endDateTimeUTC = startDateTimeUTC.plusMinutes(duration);
            link = GoogleCalendarUtil.generateLink(title, shortDescription, eventLocation, startDateTimeUTC,
                    endDateTimeUTC);
            googleCalendarLink = link;
        }
        return googleCalendarLink;
    }

}
