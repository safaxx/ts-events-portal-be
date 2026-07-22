package com.techsisters.gatherly.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RecurrenceRequest {
    private Boolean enabled;
    private String frequency;
    private List<String> weeklyDays;
    private Integer monthlyDay; // 10
    private LocalDate endDate;


}
