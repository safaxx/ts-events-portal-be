package com.techsisters.gatherly.service;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.entity.Event;
import com.techsisters.gatherly.entity.EventRSVP;
import com.techsisters.gatherly.mapper.EventMapper;
import com.techsisters.gatherly.repository.EventRSVPRepository;
import com.techsisters.gatherly.repository.EventRepository;
import com.techsisters.gatherly.request.EventRSVPRequest;
import com.techsisters.gatherly.request.EventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
