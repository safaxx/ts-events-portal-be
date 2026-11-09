package com.techsisters.gatherly.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.resend.core.exception.ResendException;
import jakarta.mail.MessagingException;

@ExtendWith(MockitoExtension.class)
public class TestEmailService {

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailDeliveryService emailDeliveryService;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void test_sendOtpEmailDelegatesRenderedTemplate() throws MessagingException, ResendException {
        when(templateEngine.process(eq("otp-email.html"), any(Context.class)))
                .thenReturn("rendered OTP email");

        emailService.sendOtpEmail("noor@example.com", "Noor", "123456");

        verify(emailDeliveryService).send("noor@example.com", "[Tech Sisters] OTP Sign-in", "rendered OTP email");
    }

    @Test
    public void test_sendMessage() {
        emailService.sendMessage("noor@example.com", "Test Subject", "Test Message");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

}
