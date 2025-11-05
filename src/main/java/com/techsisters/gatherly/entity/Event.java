package com.techsisters.gatherly.entity;

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

    private String description;

    private OffsetDateTime eventDateTime;

    private LocalDateTime createdDate = LocalDateTime.now(); // auto set

    private String timezone;

    private String eventType;

    private String eventHostEmail;

    private String organizerEmail;

    private String tags;

    private Integer duration;

    private String eventLink;
    private String createdBy;
    private LocalDateTime updatedDate;

//    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<EventRSVP> rsvps = new ArrayList<>();

}
