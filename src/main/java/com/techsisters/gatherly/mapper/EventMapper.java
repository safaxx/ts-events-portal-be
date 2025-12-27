package com.techsisters.gatherly.mapper;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.dto.EventRSVPDTO;
import com.techsisters.gatherly.entity.Event;
import com.techsisters.gatherly.entity.EventRSVP;
import com.techsisters.gatherly.entity.User;
import com.techsisters.gatherly.repository.EventRSVPRepository;
import com.techsisters.gatherly.repository.UserRepository;
import com.techsisters.gatherly.request.EventRequest;
import com.techsisters.gatherly.util.DateUtil;
import com.techsisters.gatherly.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final EventRSVPRepository eventRSVPRepo;
    private final UserRepository userRepository;

    private String getUserName(String useremail){
        Optional<User> user = userRepository.findByEmail(useremail);
        if(user.isPresent()) return user.get().getName();
        return "";
    }

    public Event convertToEntity(EventRequest request) {

        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setShortDescription(request.getShortDescription());
        event.setLongDescription(request.getLongDescription());
        event.setOrganizerEmail(request.getOrganizerEmail());
        event.setTimezone(request.getTimezone());
        event.setEventType(request.getEventType());
        event.setEventHostEmail(request.getEventHostEmail());
        event.setEventHostName(request.getEventHostName());
        event.setEventLink(request.getEventLink());
        event.setEventLocation(request.getEventLocation());
        event.setCreatedBy(request.getCreatedBy());
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
        String currentUserEmail = SecurityUtil.getCurrentUserEmail();

        for (Event e : events) {
            EventDTO dto = new EventDTO();
            dto.setTitle(e.getTitle());
            dto.setShortDescription(e.getShortDescription());
            //dto.setLongDescription(e.getLongDescription());
            dto.setTimezone(e.getTimezone());
            dto.setEventType(e.getEventType());
            dto.setEventHostEmail(e.getEventHostEmail());
            dto.setEventHostName(e.getEventHostName());
            dto.setOrganizerEmail(e.getOrganizerEmail());
            dto.setEventDateTime(e.getEventDateTime());
            dto.setDuration(e.getDuration());
            dto.setEventId(e.getEventId());
            dto.setTags(e.getTags());
            dto.setCreatedBy(e.getCreatedBy());
            dto.setAllRSVPs(countAllRSVPs(e.getEventId()));
            dto.setCurrentUserRSVP(checkUserRSVP(e, currentUserEmail));
            dto.setEventLocation(e.getEventLocation());
            dto.setEventLink(e.getEventLink());
            list.add(dto);
        }
        return list;
    }

    private Integer countAllRSVPs(Long eventId) {
        List<EventRSVP> rsvps = eventRSVPRepo.findAllByEvent_EventId(eventId);
        return rsvps.size();
    }

    public EventDTO getEventDetails(Event e) {
        String currentUserEmail = SecurityUtil.getCurrentUserEmail();

        EventDTO dto = new EventDTO();
        dto.setTitle(e.getTitle());
        dto.setShortDescription(e.getShortDescription());
        dto.setLongDescription(e.getLongDescription());
        dto.setTimezone(e.getTimezone()); //convert to user's TZ and show
        dto.setEventType(e.getEventType());
        dto.setEventHostEmail(e.getEventHostEmail());
        dto.setOrganizerEmail(e.getOrganizerEmail());
        dto.setEventDateTime(e.getEventDateTime());
        dto.setEventHostName(e.getEventHostName());
        dto.setDuration(e.getDuration());
        dto.setEventId(e.getEventId());
        dto.setTags(e.getTags());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setAllRSVPs(countAllRSVPs(e.getEventId()));
        dto.setEventLocation(e.getEventLocation());
        dto.setEventLink(e.getEventLink());
        // Check if current user has RSVP'd
        dto.setCurrentUserRSVP(checkUserRSVP(e, currentUserEmail));
        return dto;
    }

    private Boolean checkUserRSVP(Event event, String currentUserEmail) {
        if ("anonymous".equals(currentUserEmail)) {
            return false;
        }
        Optional<EventRSVP> e = eventRSVPRepo.findByEventAndUserEmail(event, currentUserEmail);
        return e.isPresent() && e.get().isRsvpStatus();
    }

    public List<EventRSVPDTO> getRSVPs(List<EventRSVP> rsvps) {
        List<EventRSVPDTO> list = new ArrayList<>();
        for (EventRSVP e : rsvps) {
            EventRSVPDTO dto = new EventRSVPDTO();
            dto.setRsvpId(e.getRsvpId());
            dto.setEventID(e.getEvent().getEventId());
            dto.setUserEmail(e.getUserEmail());
            dto.setRsvpStatus(e.isRsvpStatus());
            dto.setRsvpDate(e.getRsvpDate());
            list.add(dto);
        }
        return list;
    }
}
