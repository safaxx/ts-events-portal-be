package com.techsisters.gatherly.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentorAvailabilityRequest {

    @NotNull(message = "available is required")
    private Boolean available;
}
