package com.techsisters.gatherly.dto;

import com.techsisters.gatherly.entity.Event;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventRSVPDTO {
    private Long rsvpId;
    private Long eventID;
    private String userEmail;
    private boolean rsvpStatus;// true = attending, false = cancelled
    private LocalDateTime rsvpDate;
}
