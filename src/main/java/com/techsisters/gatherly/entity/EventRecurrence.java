package com.techsisters.gatherly.entity;

import com.techsisters.gatherly.dto.RecurrenceFrequency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "event_recurrences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRecurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recurrenceId;

    @OneToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private Boolean enabled;

    @Enumerated(EnumType.STRING)
    private RecurrenceFrequency frequency;

    @ElementCollection
    @CollectionTable(
            name = "event_recurrence_weekly_days",
            joinColumns = @JoinColumn(name = "recurrence_id")
    )
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> weeklyDays;

    private Integer monthlyDay;

    private LocalDate endDate;
}