package com.techsisters.gatherly.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;

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
    private String tags;
    private Integer duration;
    private String eventLink;
    private String eventLocation;
    private String createdBy;
    private LocalDateTime updatedDate;

//    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<EventRSVP> rsvps = new ArrayList<>();

}
