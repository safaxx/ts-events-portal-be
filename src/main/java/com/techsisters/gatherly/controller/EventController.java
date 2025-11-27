package com.techsisters.gatherly.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.dto.EventRSVPDTO;
import com.techsisters.gatherly.dto.ResponseDTO;
import com.techsisters.gatherly.entity.Event;
import com.techsisters.gatherly.entity.EventRSVP;
import com.techsisters.gatherly.request.EventRSVPRequest;
import com.techsisters.gatherly.request.EventRequest;
import com.techsisters.gatherly.response.AllEventsResponse;
import com.techsisters.gatherly.response.UserCreatedEventsResponse;
import com.techsisters.gatherly.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        if (event != null) {
            response.setSuccess(true);
            response.setMessage("Event created successfully.");
        } else {
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
        EventRSVP rsvp = eventService.createRSVP(rsvpRequest);
        if (rsvp != null) {
            response.setSuccess(true);
            response.setMessage("RSVP successful!");
        } else {
            response.setSuccess(false);
        }
        return response;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-rsvps")
    public AllEventsResponse getUserRSVPs(@AuthenticationPrincipal UserDetails userDetails) {
        List<EventDTO> events = eventService.getUserRSVPs(userDetails.getUsername());
        AllEventsResponse response = new AllEventsResponse();
        if (events != null) {
            response.setEvents(events);
            response.setMessage("Data returned successfully");
            response.setSuccess(true);
        } else {
            response.setSuccess(false);
            response.setMessage("No Events found");
        }
        return response;

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-rsvps")
    public List<EventRSVPDTO> getAllEventRSVPs() {
        return eventService.getAllEventsRSVPs();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-created")
    public UserCreatedEventsResponse getUserCreatedEvents(@AuthenticationPrincipal UserDetails userDetails){
        UserCreatedEventsResponse response = new UserCreatedEventsResponse();
        List<EventDTO> createdEvents =  eventService.getAllUserCreatedEvents(userDetails.getUsername());
        if(createdEvents != null){
            response.setEvents(createdEvents);
            response.setMessage("Data retrieved successfully");
            response.setSuccess(true);
        }else{
            response.setSuccess(false);
            response.setMessage("No events found");
        }
        return response;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseDTO updateEvent(
            @RequestParam Long eventId,
            @Valid @RequestBody EventRequest eventRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        ResponseDTO response = new ResponseDTO();

        try {
            String currentUserEmail = userDetails.getUsername();
            eventService.validateEventEditPermission(eventId, currentUserEmail);
            Event updatedEvent = eventService.updateEvent(eventId, eventRequest);
            response.setSuccess(true);
            response.setMessage("Event updated successfully");

        } catch (IllegalArgumentException e) {
            // Event not found or invalid data
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        } catch (IllegalStateException e) {
            // Permission or validation errors
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            // Unexpected errors
            response.setSuccess(false);
            response.setMessage("Error updating event: " + e.getMessage());
        }

        return response;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{eventId}")
    public ResponseDTO deleteEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails userDetails) {

        ResponseDTO response = new ResponseDTO();

        try {
            String currentUserEmail = userDetails.getUsername();

            // Validate edit permission (same rules apply for delete)
            eventService.validateEventEditPermission(eventId, currentUserEmail);

            // Delete the event
            eventService.deleteEvent(eventId);

            response.setSuccess(true);
            response.setMessage("Event deleted successfully");

        } catch (IllegalArgumentException e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        } catch (IllegalStateException e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error deleting event: " + e.getMessage());

        }

        return response;
    }
}
