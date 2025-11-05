package com.techsisters.gatherly.response;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.dto.ResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AllEventsResponse extends ResponseDTO {
    List<EventDTO> events = new ArrayList<>();
}
