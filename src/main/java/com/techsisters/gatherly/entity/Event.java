package com.techsisters.gatherly.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
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
    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private EventRecurrence recurrence;

    // Shared across all occurrences generated from the same recurrence rule.
    // Null for one-off (non-recurring) events. Only the first occurrence in a
    // series carries the actual EventRecurrence rule (see above), since that's
    // a strict one-to-one; every other occurrence just carries this group id.
    private String recurrenceGroupId;

    // @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<EventRSVP> rsvps = new ArrayList<>();

}