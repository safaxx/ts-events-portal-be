package com.techsisters.gatherly.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GoogleCalendarEventRequest {

    private Long eventId;

    private String returnUrl;

}
