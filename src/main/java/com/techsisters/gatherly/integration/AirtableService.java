package com.techsisters.gatherly.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import feign.Feign;
import feign.Request.Options;
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
public class AirtableService {

    @Value("${techsisters.airtable.url}")
    private String integUrl;

    @Value("${techsisters.airtable.apiKey}")
    private String apiKey;

    @Value("${techsisters.airtable.baseId}")
    private String baseId;

    @Value("${techsisters.airtable.tableName}")
    private String tableName;

    AirtableFeignClient client;

    @PostConstruct
    public void init() {
        log.info("Initializing Airtable Service");

        client = Feign.builder().client(new OkHttpClient()).encoder(new FormEncoder(new JacksonEncoder()))
                .decoder(new JacksonDecoder())
                .options(new Options(5000, 300000))
                .retryer(Retryer.NEVER_RETRY)
                .logger(new Slf4jLogger(AirtableFeignClient.class)).logLevel(feign.Logger.Level.FULL)
                .target(AirtableFeignClient.class, integUrl);

    }

    public String getList() {
        log.info("Getting List from Airtable");
        return client.getList(apiKey, baseId, tableName);
    }

}
