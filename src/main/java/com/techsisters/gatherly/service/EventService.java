package com.techsisters.gatherly.service;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.dto.EventRSVPDTO;
import com.techsisters.gatherly.entity.Event;
import com.techsisters.gatherly.entity.EventRSVP;
import com.techsisters.gatherly.mapper.EventMapper;
import com.techsisters.gatherly.repository.EventRSVPRepository;
import com.techsisters.gatherly.repository.EventRepository;
import com.techsisters.gatherly.request.EventRSVPRequest;
import com.techsisters.gatherly.request.EventRequest;
import com.techsisters.gatherly.response.AllEventsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final EventRSVPRepository eventRSVPRepository;

    public Event createEvent(EventRequest eventRequest) {
        Event event = eventMapper.convertToEntity(eventRequest);
        return eventRepository.save(event);
    }

    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return eventMapper.getEvents(events);
    }
    public EventRSVP createRSVP(EventRSVPRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Check if user already RSVPed
        Optional<EventRSVP> existing = eventRSVPRepository.findByEventAndUserEmail(event, request.getUserEmail());
        if (existing.isPresent()) {
            throw new IllegalStateException("User already RSVPed to this event");
        }

        EventRSVP rsvp = new EventRSVP();
        rsvp.setEvent(event);
        rsvp.setUserEmail(request.getUserEmail());
        rsvp.setRsvpStatus(request.getRsvp());
        return eventRSVPRepository.save(rsvp);
    }
    private Event getEventById(Long eventId){
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    public EventDTO getEventDetails(Long eventId) {
        Event event = getEventById(eventId);
        return eventMapper.getEventDetails(event);
    }


    public List<EventDTO> getUserRSVPs(String userEmail) {
        List<EventRSVP> eventRSVPs = eventRSVPRepository.findByUserEmailAndRsvpStatus(userEmail, true);
        List<Long> eventIDs = eventRSVPs.stream().map(e -> e.getEvent().getEventId()).toList();

        List<Event> events = eventRepository.findByEventIdIn(eventIDs);
        return eventMapper.getEvents(events);
    }

    public List<EventRSVPDTO> getAllEventsRSVPs() {
        List<EventRSVP> rsvps =  eventRSVPRepository.findAll();
        return eventMapper.getRSVPs(rsvps);
    }

    public List<EventDTO> getAllUserCreatedEvents(String username) {
        List<Event> events = eventRepository.findByCreatedBy(username);
        return eventMapper.getEvents(events);
    }

    /**
     * Check if a user is the owner of an event
   */
    public boolean isEventOwner(Long eventId, String userEmail) {
        Event event = getEventById(eventId);
        if (event == null) {
            return false;
        }
        return userEmail.equals(event.getCreatedBy()) ||
                userEmail.equals(event.getOrganizerEmail());
    }

    /**
     * Validate if event can be edited
     */
    public void validateEventEditPermission(Long eventId, String userEmail) {
        Event event = getEventById(eventId);
        if (!isEventOwner(eventId, userEmail)) {
            throw new IllegalStateException("You are not authorized to edit this event");
        }
        if (event.getEventDateTime().isBefore(OffsetDateTime.now())) {
            throw new IllegalStateException("Cannot edit past events");
        }
    }

    /**
     * Update an existing event
     */
    public Event updateEvent(Long eventId, EventRequest eventRequest) {
        // Fetch the existing event
        Event existingEvent = getEventById(eventId);
        existingEvent.setTitle(eventRequest.getTitle());
        existingEvent.setShortDescription(eventRequest.getShortDescription());
        existingEvent.setLongDescription((eventRequest.getLongDescription()));
        existingEvent.setEventType(eventRequest.getEventType());
        existingEvent.setEventHostEmail(eventRequest.getEventHostEmail());
        existingEvent.setTags(eventRequest.getTags());
        existingEvent.setDuration(eventRequest.getDuration());
        existingEvent.setEventLocation(eventRequest.getEventLocation());
        existingEvent.setEventLink(eventRequest.getEventLink());
        try {
            existingEvent.setEventDateTime(OffsetDateTime.parse(eventRequest.getEventDateTime()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid datetime format. Use ISO 8601 format (e.g. 2025-11-02T18:00:00+05:30)");
        }
        if (eventRequest.getTimezone() != null && !eventRequest.getTimezone().isEmpty()) {
            existingEvent.setTimezone(eventRequest.getTimezone());
        }
        existingEvent.setUpdatedDate(LocalDateTime.now());
        return eventRepository.save(existingEvent);
    }

    /**
     * Delete an event
     * @param eventId Event ID to delete
     */
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        List<EventRSVP> rsvps = eventRSVPRepository.findAllByEvent_EventId(eventId);
        if (!rsvps.isEmpty()) {
            eventRSVPRepository.deleteAll(rsvps);
        }

        eventRepository.delete(event);
    }
}