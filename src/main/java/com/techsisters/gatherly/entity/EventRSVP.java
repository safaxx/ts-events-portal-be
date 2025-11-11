package com.techsisters.gatherly.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;



@Entity
@Table(name = "event_rsvps")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRSVP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rsvpId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private String userEmail;

    private boolean rsvpStatus;// true = attending, false = cancelled

    private LocalDateTime rsvpDate = LocalDateTime.now();
}

