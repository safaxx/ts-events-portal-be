package com.techsisters.gatherly.service;

import java.util.Map;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.resend.core.exception.ResendException;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {
    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final EmailDeliveryService emailDeliveryService;

    public void sendMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void sendOtpEmail(String to, String name, String otp) throws MessagingException, ResendException {
        Map<String, Object> model = Map.of("name", name, "otp", otp);

        String subject = "Your OTP for login - Tech Sisters Portal";
        String templateName = "otp-email.html";

        log.info("Sending OTP email to {}", to);

        sendEmailWithTemplate(to, subject, templateName, model);

        log.info("Successfully sent OTP email to {}", to);
    }

    @Async
    public void sendEventReminderEmail(
            String to,
            String userName,
            String eventTitle,
            String eventDescription,
            String eventDateTime,
            String eventType,
            String eventLocation,
            String eventLink,
            Integer duration) throws MessagingException, ResendException {

        Map<String, Object> model = Map.of(
                "userName", userName != null ? userName : "Tech Sister",
                "eventTitle", eventTitle,
                "eventDescription", eventDescription != null ? eventDescription : "",
                "eventDateTime", eventDateTime,
                "eventType", eventType,
                "eventLocation", eventLocation != null ? eventLocation : "",
                "eventLink", eventLink != null ? eventLink : "",
                "duration", duration != null ? duration : 60);

        String subject = "⏰ Reminder: " + eventTitle + " starts in 30 minutes!";
        String templateName = "event-reminder-email.html";

        log.info("Sending event reminder email to {} for event: {}", to, eventTitle);

        sendEmailWithTemplate(to, subject, templateName, model);

        log.info("Successfully sent event reminder email to {}", to);
    }

    @Async
    public void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, Object> templateModel)
            throws MessagingException, ResendException {

        // 1. Create the Thymeleaf context
        Context context = new Context();
        context.setVariables(templateModel); // Pass all variables from the map

        // 2. Process the template and get the HTML content as a string
        // This will find 'email-template.html' in 'src/main/resources/templates/'
        String htmlBody = templateEngine.process(templateName, context); // This will find 'email-template.html' in
                                                                         // 'src/main/resources/templates/'

        emailDeliveryService.send(to, subject, htmlBody);
    }
}
