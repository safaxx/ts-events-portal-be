package com.techsisters.gatherly.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;
    private String title;

    @Column(length = 200, nullable = false)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String longDescription;
    private OffsetDateTime eventDateTime;
    private LocalDateTime createdDate = LocalDateTime.now();
    private String timezone;
    private String eventType;
    private String eventHostEmail;
    private String organizerEmail;
    //private String tags;
    private Integer duration;
    private String eventLink;
    private String eventLocation;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String eventHostName;
    private boolean reminderSent;

    // @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<EventRSVP> rsvps = new ArrayList<>();

}
