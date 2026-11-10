package com.techsisters.gatherly.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Profile("!local")
@Slf4j
public class MailgunEmailDeliveryService implements EmailDeliveryService {

    @Value("${smtp.mailgun.apiKey}")
    private String mailgunApiKey;

    @Value("${smtp.mailgun.domain}")
    private String mailgunDomain;

    @Override
    public void send(String to, String subject, String htmlBody) {
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
