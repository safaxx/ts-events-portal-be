package com.techsisters.gatherly.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.dto.EventRSVPDTO;
import com.techsisters.gatherly.entity.Event;
import com.techsisters.gatherly.entity.EventRSVP;
import com.techsisters.gatherly.mapper.EventMapper;
import com.techsisters.gatherly.repository.EventRSVPRepository;
import com.techsisters.gatherly.repository.EventRepository;
import com.techsisters.gatherly.repository.EventSpecification;
import com.techsisters.gatherly.request.EventRSVPRequest;
import com.techsisters.gatherly.request.EventRequest;
import com.techsisters.gatherly.request.RecurrenceRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final EventRSVPRepository eventRSVPRepository;

    // Safety cap so a bad/missing end date can't generate unbounded rows.
    private static final int MAX_RECURRING_OCCURRENCES = 365;

    public Event createEvent(EventRequest eventRequest) {
        Event event = eventMapper.convertToEntity(eventRequest);
        event.setReminderSent(false);

        RecurrenceRequest recurrenceRequest = eventRequest.getRecurrence();
        boolean isRecurring = recurrenceRequest != null && Boolean.TRUE.equals(recurrenceRequest.getEnabled());

        if (!isRecurring) {
            return eventRepository.save(event);
        }

        return createRecurringEvent(event, recurrenceRequest);
    }

    /**
     * Creates a recurring series: the first occurrence is saved with the
     * EventRecurrence rule attached (satisfies the strict one-to-one), and
     * every subsequent occurrence is a plain Event row sharing the same
     * recurrenceGroupId.
     */
    private Event createRecurringEvent(Event firstOccurrence, RecurrenceRequest recurrenceRequest) {
        if (recurrenceRequest.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required for recurring events");
        }
        if (recurrenceRequest.getFrequency() == null || recurrenceRequest.getFrequency().isBlank()) {
            throw new IllegalArgumentException("Frequency is required for recurring events");
        }

        OffsetDateTime firstDateTime = firstOccurrence.getEventDateTime();
        if (recurrenceRequest.getEndDate().isBefore(firstDateTime.toLocalDate())) {
            throw new IllegalArgumentException("Recurrence end date cannot be before the event's start date");
        }

        List<OffsetDateTime> occurrenceDates = generateOccurrenceDates(firstDateTime, recurrenceRequest);

        String recurrenceGroupId = UUID.randomUUID().toString();
        firstOccurrence.setRecurrenceGroupId(recurrenceGroupId);

        Event savedFirstOccurrence = eventRepository.save(firstOccurrence);

        List<Event> remainingOccurrences = new ArrayList<>();
        for (int i = 1; i < occurrenceDates.size(); i++) {
            remainingOccurrences.add(
                    eventMapper.cloneForOccurrence(savedFirstOccurrence, occurrenceDates.get(i), recurrenceGroupId));
        }
        if (!remainingOccurrences.isEmpty()) {
            eventRepository.saveAll(remainingOccurrences);
        }

        return savedFirstOccurrence;
    }

    /**
     * Computes every occurrence date/time for a recurrence rule, starting
     * from (and including) the first occurrence, up to and including the
     * rule's end date, capped at MAX_RECURRING_OCCURRENCES.
     *
     * NOTE: assumes RecurrenceFrequency has DAILY / WEEKLY / MONTHLY constants.
     * Adjust the switch below if your enum uses different names.
     */
    private List<OffsetDateTime> generateOccurrenceDates(OffsetDateTime start, RecurrenceRequest recurrenceRequest) {
        List<OffsetDateTime> dates = new ArrayList<>();
        LocalDate endDate = recurrenceRequest.getEndDate();
        String frequency = recurrenceRequest.getFrequency().trim().toUpperCase();

        switch (frequency) {
            case "DAILY" -> {
                OffsetDateTime current = start;
                while (!current.toLocalDate().isAfter(endDate) && dates.size() < MAX_RECURRING_OCCURRENCES) {
                    dates.add(current);
                    current = current.plusDays(1);
                }
            }
            case "WEEKLY" -> {
                Set<DayOfWeek> targetDays = parseWeeklyDays(recurrenceRequest.getWeeklyDays(), start.getDayOfWeek());
                OffsetDateTime current = start;
                while (!current.toLocalDate().isAfter(endDate) && dates.size() < MAX_RECURRING_OCCURRENCES) {
                    if (targetDays.contains(current.getDayOfWeek())) {
                        dates.add(current);
                    }
                    current = current.plusDays(1);
                }
            }
            case "MONTHLY" -> {
                int dayOfMonth = recurrenceRequest.getMonthlyDay() != null
                        ? recurrenceRequest.getMonthlyDay()
                        : start.getDayOfMonth();
                OffsetDateTime current = start;
                while (!current.toLocalDate().isAfter(endDate) && dates.size() < MAX_RECURRING_OCCURRENCES) {
                    dates.add(current);
                    current = nextMonthlyOccurrence(current, dayOfMonth);
                }
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported recurrence frequency: " + recurrenceRequest.getFrequency());
        }

        if (dates.isEmpty()) {
            // Rule produced no matches before the end date (e.g. weekly day
            // already passed for this week) — always keep the original date.
            dates.add(start);
        }
        return dates;
    }

    private Set<DayOfWeek> parseWeeklyDays(List<String> weeklyDays, DayOfWeek fallback) {
        if (weeklyDays == null || weeklyDays.isEmpty()) {
            return EnumSet.of(fallback);
        }
        Set<DayOfWeek> result = EnumSet.noneOf(DayOfWeek.class);
        for (String day : weeklyDays) {
            result.add(DayOfWeek.valueOf(day.trim().toUpperCase()));
        }
        return result;
    }

    /**
     * Given the current occurrence, finds the same day-of-month in the
     * following month, clamping to the last valid day if the target day
     * doesn't exist there (e.g. day 31 in a 30-day month).
     */
    private OffsetDateTime nextMonthlyOccurrence(OffsetDateTime current, int targetDay) {
        OffsetDateTime firstOfNextMonth = current.withDayOfMonth(1).plusMonths(1);
        int lastDayOfNextMonth = firstOfNextMonth.toLocalDate().lengthOfMonth();
        int actualDay = Math.min(targetDay, lastDayOfNextMonth);
        return firstOfNextMonth
                .withDayOfMonth(actualDay)
                .withHour(current.getHour())
                .withMinute(current.getMinute())
                .withSecond(current.getSecond());
    }

    public Page<EventDTO> getAllEvents(int pageNo, int pageSize, EventDTO.ListType listType, String searchQuery) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("eventDateTime").descending());

        Page<Event> events = eventRepository.findAll(EventSpecification.filter(listType, searchQuery), paging);

        List<Event> eventList = events.getContent();

        List<EventDTO> eventDTOs = eventMapper.getEvents(eventList);
        return new PageImpl<>(eventDTOs, paging, events.getTotalElements());
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

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    public EventDTO getEventDetails(Long eventId) {
        Event event = getEventById(eventId);
        return eventMapper.getEventDetails(event);
    }

    public Page<EventDTO> getUserRSVPs(String userEmail, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("rsvpDate").descending());

        Page<EventRSVP> eventRSVPs = eventRSVPRepository.findByUserEmailAndRsvpStatus(userEmail, true, paging);
        List<Long> eventIDs = eventRSVPs.getContent().stream().map(e -> e.getEvent().getEventId()).toList();

        List<Event> events = eventRepository.findByEventIdIn(eventIDs);
        List<EventDTO> eventDTOs = eventMapper.getEvents(events);

        return new PageImpl<>(eventDTOs, paging, eventRSVPs.getTotalElements());
    }

    public List<EventRSVPDTO> getAllEventsRSVPs() {
        List<EventRSVP> rsvps = eventRSVPRepository.findAll();
        return eventMapper.getRSVPs(rsvps);
    }

    public Page<EventDTO> getAllUserCreatedEvents(String username, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("eventDateTime").descending());

        Page<Event> events = eventRepository.findByCreatedBy(username, paging);
        List<Event> eventList = events.getContent();

        List<EventDTO> eventDTOs = eventMapper.getEvents(eventList);
        return new PageImpl<>(eventDTOs, paging, events.getTotalElements());
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
        // existingEvent.setTags(eventRequest.getTags());
        existingEvent.setDuration(eventRequest.getDuration());
        existingEvent.setEventLocation(eventRequest.getEventLocation());
        existingEvent.setEventLink(eventRequest.getEventLink());

        try {
            existingEvent.setEventDateTime(OffsetDateTime.parse(eventRequest.getEventDateTime()));
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid datetime format. Use ISO 8601 format (e.g. 2025-11-02T18:00:00+05:30)");
        }
        if (eventRequest.getTimezone() != null && !eventRequest.getTimezone().isEmpty()) {
            existingEvent.setTimezone(eventRequest.getTimezone());
        }
        existingEvent.setUpdatedDate(LocalDateTime.now());

        // set reminder to false to trigger the notification again
        existingEvent.setReminderSent(false);
        return eventRepository.save(existingEvent);
    }

    /**
     * Delete an event
     * 
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

    public List<Event> getEventsForReminder(OffsetDateTime start, OffsetDateTime end) {

        return eventRepository
                .findByEventDateTimeBetweenAndReminderSentFalse(start, end);
    }
}