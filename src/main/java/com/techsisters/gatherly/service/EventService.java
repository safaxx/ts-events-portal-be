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

    public EventDTO getEventDetails(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
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
}
