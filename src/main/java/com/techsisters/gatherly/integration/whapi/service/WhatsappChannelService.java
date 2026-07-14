package com.techsisters.gatherly.integration.whapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.techsisters.gatherly.integration.whapi.dto.WhapiMessageRequest;
import com.techsisters.gatherly.util.CommonUtil;

import feign.Feign;
import feign.Request.Options;
import feign.Response;
import feign.Retryer;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WhatsappChannelService {

    WhapiFeignClient whapiClient;

    @Value("${whapi.api.token}")
    private String botToken;

    @Value("${whatsapp.group.id}")
    private String groupId;

    @PostConstruct
    public void init() {
        log.info("Initializing Whatsapp channel Service");

        whapiClient = Feign.builder().client(new OkHttpClient()).encoder(new FormEncoder(new JacksonEncoder()))
                .decoder(new JacksonDecoder())
                .options(new Options(5000, 300000))
                .retryer(Retryer.NEVER_RETRY)
                .logger(new Slf4jLogger(WhapiFeignClient.class)).logLevel(feign.Logger.Level.FULL)
                .target(WhapiFeignClient.class, "https://gate.whapi.cloud");

    }

    public void sendMsgToGroup(String body) {
        String authHeader = "Bearer " + botToken;

        WhapiMessageRequest request = WhapiMessageRequest.builder().to(groupId).body(body).build();

        try {
            Response response = whapiClient.sendTextMessage(authHeader, request);
            log.debug("Message sent successfully! Response: {}", CommonUtil.convertToJsonString(response.body()));

        } catch (Exception e) {
            log.error("Failed to send WhatsApp message via Feign: " + e.getMessage());
            throw e;
        }

    }

}
