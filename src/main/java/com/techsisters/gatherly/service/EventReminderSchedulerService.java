package com.techsisters.gatherly.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.resend.core.exception.ResendException;
import com.techsisters.gatherly.entity.Event;
import com.techsisters.gatherly.entity.EventRSVP;
import com.techsisters.gatherly.entity.User;
import com.techsisters.gatherly.integration.whapi.service.WhatsappChannelService;
import com.techsisters.gatherly.repository.EventRSVPRepository;
import com.techsisters.gatherly.repository.EventRepository;
import com.techsisters.gatherly.repository.UserRepository;

import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventReminderSchedulerService {

    private final EventRepository eventRepository;
    private final EventRSVPRepository eventRSVPRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final WhatsappChannelService whatsappChannelService;
    private final EventService eventService;

    private static final String TECH_SISTERS_EVENTS_URL = "https://www.tech-sisters.com/events";

    // Run every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    public void sendEventReminders() {
        log.info("Running event reminder scheduler...");

        try {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime oneHourFromNow = now.plusHours(1);

            List<Event> upcomingEvents = eventService.getEventsForReminder(now, oneHourFromNow);
            log.info("Found {} events starting in 60 minutes", upcomingEvents.size());

            for (Event event : upcomingEvents) {
                sendRemindersForEvent(event);
                sendWhatsappNotification(event);

                event.setReminderSent(true);
                eventRepository.save(event);
            }

        } catch (Exception e) {
            log.error("Error in event reminder scheduler: {}", e.getMessage(), e);
        }
    }

    private void sendWhatsappNotification(Event event) {
        log.info("Sending Whatsapp msg for event: {} (ID: {})", event.getTitle(), event.getEventId());

        String timeLeft = getTimeLeft(event.getEventDateTime());

        StringBuilder body = new StringBuilder();
        body.append("🔔 *").append(event.getTitle()).append("* is starting in ").append(timeLeft).append("\n\n");
        body.append(event.getShortDescription()).append("\n\n");

        body.append("📅 ").append(formatEventDateTime(event.getEventDateTime(), event.getTimezone()))
                .append("\n");

        body.append("🔗 ").append(StringUtils.isNotBlank(event.getEventLink()) ? event.getEventLink() : "N/A")
                .append("\n\n");

        // Events Portal link
        body.append("🚀 Check out our upcoming events here:\n")
                .append(TECH_SISTERS_EVENTS_URL);

        whatsappChannelService.sendMsgToGroup(body.toString());
    }

    private void sendRemindersForEvent(Event event) {
        log.info("Sending reminders for event: {} (ID: {})", event.getTitle(), event.getEventId());

        // Get all RSVPs for this event where status is true (attending)
        List<EventRSVP> rsvps = eventRSVPRepository.findByEventAndRsvpStatus(event, true);

        log.info("Found {} confirmed RSVPs for event: {}", rsvps.size(), event.getTitle());

        for (EventRSVP rsvp : rsvps) {
            try {
                // Get user details
                User user = userRepository.findByEmail(rsvp.getUserEmail()).orElse(null);
                if (user == null) {
                    log.warn("User not found for email: {}", rsvp.getUserEmail());
                    continue;
                }
                // Format event date time for display
                String formattedDateTime = formatEventDateTime(event.getEventDateTime(), event.getTimezone());
                // Send reminder email
                emailService.sendEventReminderEmail(
                        user.getEmail(),
                        user.getName(),
                        event.getTitle(),
                        event.getShortDescription(),
                        formattedDateTime,
                        event.getEventType(),
                        event.getEventLocation(),
                        event.getEventLink(),
                        event.getDuration());

                log.info("Sent reminder email to {} for event: {}", user.getEmail(), event.getTitle());

            } catch (MessagingException | ResendException e) {
                log.error("Failed to send reminder email to {} for event {}: {}",
                        rsvp.getUserEmail(), event.getTitle(), e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error sending reminder to {}: {}",
                        rsvp.getUserEmail(), e.getMessage(), e);
            }
        }
    }

    private String formatEventDateTime(OffsetDateTime eventDateTime, String timezone) {
        try {
            ZoneId zoneId = timezone != null ? ZoneId.of(timezone) : ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = eventDateTime.atZoneSameInstant(zoneId);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a (O)");
            return zonedDateTime.format(formatter);
        } catch (Exception e) {
            log.warn("Error formatting date time, using default: {}", e.getMessage());
            return eventDateTime.toString();
        }
    }

    private String getTimeLeft(OffsetDateTime eventDateTime) {

        Duration duration = Duration.between(OffsetDateTime.now(), eventDateTime);
        long minutes = duration.toMinutes();
        String timeLeft = "";
        if (minutes <= 0) {
            timeLeft = "a few moments";
        } else if (minutes == 1) {
            timeLeft = "1 minute";
        } else if (minutes < 60) {
            timeLeft = minutes + " minutes";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            if (remainingMinutes == 0) {
                timeLeft = hours + (hours == 1 ? " hour" : " hours");
            } else {
                timeLeft = hours + (hours == 1 ? " hour" : " hours") + " and " + remainingMinutes
                        + (remainingMinutes == 1 ? " minute" : " minutes");
            }
        }

        return timeLeft;

    }
}