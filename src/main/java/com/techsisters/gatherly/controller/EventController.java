package com.techsisters.gatherly.controller;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.dto.ResponseDTO;
import com.techsisters.gatherly.entity.Event;
import com.techsisters.gatherly.entity.EventRSVP;
import com.techsisters.gatherly.request.EventRSVPRequest;
import com.techsisters.gatherly.request.EventRequest;
import com.techsisters.gatherly.response.AllEventsResponse;
import com.techsisters.gatherly.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("events")
public class EventController {
    private final EventService eventService;

    @PostMapping("/create-new")
    public ResponseDTO createEvent(@Valid @RequestBody EventRequest eventRequest) {
        Event event = eventService.createEvent(eventRequest);
        ResponseDTO response = new ResponseDTO();
        if(event != null){
            response.setSuccess(true);
            response.setMessage("Event created successfully.");
        }else{
            response.setSuccess(false);
            response.setMessage("Event creation failed");
        }
        return response;

    }

    @GetMapping("/all")
    public AllEventsResponse getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        AllEventsResponse response = new AllEventsResponse();
        if(events != null){
            response.setEvents(events);
            response.setMessage("Data returned successfully");
            response.setSuccess(true);
        }else{
            response.setSuccess(false);
            response.setMessage("No Events found");
        }
        return response;
    }
    @PostMapping("/rsvp")
    public ResponseDTO rsvpToEvent(@Valid @RequestBody EventRSVPRequest rsvpRequest) {
        ResponseDTO response = new ResponseDTO();
        EventRSVP rsvp =  eventService.createRSVP(rsvpRequest);
        if(rsvp!=null ) {
            response.setSuccess(true);
            response.setMessage("RSVP successful!");
        }else{
            response.setSuccess(false);
        }

        return response;
    }

}
