package com.techsisters.gatherly.controller;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.dto.EventRSVPDTO;
import com.techsisters.gatherly.dto.ResponseDTO;
import com.techsisters.gatherly.entity.Event;
import com.techsisters.gatherly.entity.EventRSVP;
import com.techsisters.gatherly.request.EventRSVPRequest;
import com.techsisters.gatherly.request.EventRequest;
import com.techsisters.gatherly.response.AllEventsResponse;
import com.techsisters.gatherly.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-new")
    public ResponseDTO createEvent(@Valid @RequestBody EventRequest eventRequest,
                                   @AuthenticationPrincipal UserDetails userDetails) {

        eventRequest.setCreatedBy(userDetails.getUsername());
        eventRequest.setOrganizerEmail(userDetails.getUsername());
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

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/rsvp")
    public ResponseDTO rsvpToEvent(@Valid @RequestBody EventRSVPRequest rsvpRequest,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        ResponseDTO response = new ResponseDTO();
        rsvpRequest.setUserEmail(userDetails.getUsername());
        EventRSVP rsvp =  eventService.createRSVP(rsvpRequest);
        if(rsvp!=null ) {
            response.setSuccess(true);
            response.setMessage("RSVP successful!");
        }else{
            response.setSuccess(false);
        }

        return response;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-rsvps")
    public AllEventsResponse getUserRSVPs(@AuthenticationPrincipal UserDetails userDetails){
        List<EventDTO> events = eventService.getUserRSVPs(userDetails.getUsername());
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-rsvps")
    public List<EventRSVPDTO> getAllEventRSVPs(){
        return eventService.getAllEventsRSVPs();
    }
}
