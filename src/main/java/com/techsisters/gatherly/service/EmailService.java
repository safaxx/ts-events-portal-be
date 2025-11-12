package com.techsisters.gatherly.service;

import java.util.Map;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class EmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public void sendMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void sendOtpEmail(String to, String name, String otp) throws MessagingException {
        Map<String, Object> model = Map.of("name", name, "otp", otp);

        String subject = "[Tech Sisters] OTP Sign-in";
        String templateName = "otp-email.html";

        log.info("Sending OTP email to {}", to);

        sendEmailWithTemplate(to, subject, templateName, model);

        log.info("Succesfully sent OTP email to {}", to);
    }

    @Async
    public void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, Object> templateModel)
            throws MessagingException {

        // 1. Create the Thymeleaf context
        Context context = new Context();
        context.setVariables(templateModel); // Pass all variables from the map

        // 2. Process the template and get the HTML content as a string
        // This will find 'email-template.html' in 'src/main/resources/templates/'
        String htmlBody = templateEngine.process(templateName, context);

        // 3. Send the email using the existing MimeMessage logic
        MimeMessage message = mailSender.createMimeMessage();

        // Use 'true' for multipart message, 'UTF-8' for encoding
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);

        // Use 'true' to indicate the body is HTML
        helper.setText(htmlBody, true);

        // add attachments here if needed
        // FileSystemResource file = new FileSystemResource(new File(attachmentPath));
        // helper.addAttachment(file.getFilename(), file);

        mailSender.send(message);
    }

}
