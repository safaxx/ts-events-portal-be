package com.techsisters.gatherly.mapper;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.entity.Event;
import com.techsisters.gatherly.entity.EventRSVP;
import com.techsisters.gatherly.repository.EventRSVPRepository;
import com.techsisters.gatherly.request.EventRequest;
import com.techsisters.gatherly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final EventRSVPRepository eventRSVPRepo;

    public Event convertToEntity(EventRequest request) {

        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setOrganizerEmail(request.getOrganizerEmail());
        event.setTimezone(request.getTimezone());
        event.setEventType(request.getEventType());
        event.setEventHostEmail(request.getEventHostEmail());
        //event.setEventLink(request.getEventLink());
        //event.setCreatedBy(request.getCreatedBy());
        event.setDuration(request.getDuration());
        event.setTags(request.getTags());
        event.setCreatedDate(LocalDateTime.now());

        try {
            event.setEventDateTime(OffsetDateTime.parse(request.getEventDateTime()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid datetime format. Use ISO 8601 format (e.g. 2025-11-02T18:00:00+05:30)");
        }

        return event;
    }

    public List<EventDTO> getEvents(List<Event> events) {
        List<EventDTO> list = new ArrayList<>();
        for(Event e: events){
            EventDTO dto = new EventDTO();
            dto.setTitle(e.getTitle());
            dto.setDescription(e.getDescription());
            //event.setOrganizerEmail(e.getOrganizerEmail());
            dto.setTimezone(e.getTimezone()); //convert to user's TZ and show
            dto.setEventType(e.getEventType());
            dto.setEventHostEmail(e.getEventHostEmail());
            dto.setOrganizerEmail(e.getOrganizerEmail());
            dto.setEventDateTime(e.getEventDateTime());
            dto.setDuration(e.getDuration());
            dto.setEventId(e.getEventId());
            dto.setTags(e.getTags());
            dto.setAllRSVPs(countAllRSVPs(e.getEventId()));
            list.add(dto);
        }
        return list;
    }

    private Integer countAllRSVPs(Long eventId) {
        List<EventRSVP> rsvps = eventRSVPRepo.findAllByEvent_EventId(eventId);
        return rsvps.size();
    }
}
