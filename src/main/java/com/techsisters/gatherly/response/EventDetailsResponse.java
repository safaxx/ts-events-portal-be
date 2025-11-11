package com.techsisters.gatherly.response;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.dto.ResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDetailsResponse extends ResponseDTO {
    EventDTO dto;
}
