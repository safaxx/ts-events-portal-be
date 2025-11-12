package com.techsisters.gatherly.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestEmailService {

    @Autowired
    private EmailService emailService;

    @Test
    public void test_sendOtpEmail() throws MessagingException {
        emailService.sendOtpEmail("noorsuho@gmail.com", "Noor", "123456");
    }

    @Test
    public void test_sendMessage() {
        emailService.sendMessage("noorsuho@gmail.com", "Test Subject", "Test Message");
    }

}
