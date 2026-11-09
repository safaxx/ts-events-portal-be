package com.techsisters.gatherly.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Profile("local")
@Slf4j
public class LocalEmailDeliveryService implements EmailDeliveryService {

    @Override
    public void send(String to, String subject, String htmlBody) {
        log.info("Local email mock: to={}, subject={}, rendered email:\n{}", to, subject, htmlBody);
    }
}
