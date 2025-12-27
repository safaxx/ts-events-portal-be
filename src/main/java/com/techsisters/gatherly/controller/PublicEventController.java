package com.techsisters.gatherly.controller;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.dto.ResponseDTO;
import com.techsisters.gatherly.entity.User;
import com.techsisters.gatherly.response.AllEventsResponse;
import com.techsisters.gatherly.response.EventDetailsResponse;
import com.techsisters.gatherly.service.EventService;
import com.techsisters.gatherly.service.GoogleAuthService;
import com.techsisters.gatherly.service.GoogleCalendarService;
import com.techsisters.gatherly.service.UserService;
import com.techsisters.gatherly.util.OAuthTokenNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/events")
public class PublicEventController {
    private final EventService eventService;

    private final GoogleCalendarService googleCalendarService;
    private final GoogleAuthService authService;
    private final UserService userService;

    @GetMapping("/add-google-calendar-event")
    public ResponseEntity<Object> createCalendarEvent(@RequestParam("eventId") Long eventId,
            @RequestParam(value = "returnUrl", required = false) String returnUrl,
            @RequestParam(value = "email", required = false) String email) {

        log.info("Creating Google Calendar event {}", eventId);

        EventDTO eventDTO = eventService.getEventDetails(eventId);

        ZonedDateTime startDateTimeUTC = eventDTO.getEventDateTime().toZonedDateTime();
        ;
        ZonedDateTime endDateTimeUTC = startDateTimeUTC.plusMinutes(eventDTO.getDuration());

        ResponseDTO response = new ResponseDTO();
        try {
            com.google.api.services.calendar.model.Event calEvent = googleCalendarService.createEvent(
                    email, eventDTO.getTitle(),
                    eventDTO.getLongDescription(), startDateTimeUTC, endDateTimeUTC);

            response.setSuccess(true);
            response.setMessage(calEvent.getHtmlLink());

            // **Redirect the user's browser to the Google URL**
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", calEvent.getHtmlLink())
                    .build();

        } catch (IOException e) {
            log.error("Error in creating google calendar event", e);
            response.setSuccess(false);
            response.setMessage("Error in creating google calendar event: " + e.getMessage());
        } catch (OAuthTokenNotFoundException e) {

            log.error("Token not found, initiating Google OAuth flow", e.getMessage());
            // FAILURE (NO TOKEN): Initiate the Google Authorization flow

            String stateToken = authService.generateState();

            StringBuilder combinedState = new StringBuilder();
            combinedState.append(stateToken);
            combinedState.append("|");
            combinedState.append(returnUrl);

            User user = userService.findByEmail(email);
            if (user != null) {
                user.setState(stateToken);
                userService.saveUser(user);
            }

            String authorizationUrl = authService.buildAuthorizationUrl(combinedState.toString());

            // **Redirect the user's browser to the Google URL**
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", authorizationUrl)
                    .build();

        }

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/all")
    public AllEventsResponse getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Page<EventDTO> eventsPage = eventService.getAllEvents(page, size);
        AllEventsResponse response = new AllEventsResponse();

        if (eventsPage != null && eventsPage.hasContent()) {
            response.setEvents(eventsPage.getContent());
            response.setTotalPages(eventsPage.getTotalPages());
            response.setTotalElements(eventsPage.getTotalElements());
            response.setCurrentPage(eventsPage.getNumber());
            response.setPageSize(eventsPage.getSize());
            response.setMessage("Data returned successfully");
            response.setSuccess(true);
        } else {
            response.setSuccess(false);
            response.setMessage("No Events found");
        }
        return response;
    }
    @GetMapping("/id")
    public EventDetailsResponse getEventDetails(@RequestParam Long eventId) {
        EventDetailsResponse response = new EventDetailsResponse();
        EventDTO dto = eventService.getEventDetails(eventId);
        if (dto != null) {
            response.setDto(dto);
            response.setSuccess(true);
            response.setMessage("Data returned successfully");
        } else {
            response.setSuccess(false);
        }
        return response;
    }
}