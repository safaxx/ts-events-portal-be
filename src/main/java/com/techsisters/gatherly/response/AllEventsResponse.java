package com.techsisters.gatherly.response;

import java.util.ArrayList;
import java.util.List;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllEventsResponse extends ResponseDTO {
    List<EventDTO> events = new ArrayList<>();
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;

}
