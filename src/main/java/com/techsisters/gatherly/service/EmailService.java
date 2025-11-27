package com.techsisters.gatherly.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import com.resend.core.exception.ResendException;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {

    @Value("${smtp.mailgun.apiKey}")
    private String mailgunApiKey;

    @Value("${smtp.mailgun.domain}")
    private String mailgunDomain;

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public void sendMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void sendOtpEmail(String to, String name, String otp) throws MessagingException, ResendException {
        Map<String, Object> model = Map.of("name", name, "otp", otp);

        String subject = "[Tech Sisters] OTP Sign-in";
        String templateName = "otp-email.html";

        log.info("Sending OTP email to {}", to);

        sendEmailWithTemplate(to, subject, templateName, model);

        log.info("Successfully sent OTP email to {}", to);
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

        MailgunMessagesApi mailgunMessagesApi = MailgunClient.config(mailgunApiKey)
                .createApi(MailgunMessagesApi.class);

        Message message = Message.builder()
                .from("noreply@" + mailgunDomain)
                .to(to)
                .subject(subject)
                .html(htmlBody)
                .build();

        MessageResponse response = mailgunMessagesApi.sendMessage(mailgunDomain, message);

        log.info("Email sent with ID: {}", response.getId());
    }

}
