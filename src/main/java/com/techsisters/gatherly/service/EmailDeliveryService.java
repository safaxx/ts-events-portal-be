package com.techsisters.gatherly.service;

public interface EmailDeliveryService {
    void send(String to, String subject, String htmlBody);

}
