package com.techsisters.gatherly.integration.whapi.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.techsisters.gatherly.entity.Event;
import com.techsisters.gatherly.service.EventService;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestWhatsappChannelService {

    @Autowired
    WhatsappChannelService whatsappChannelService;

    @Autowired
    EventService eventService;

    @Test
    public void test_getEvents() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime thirtyMinutesFromNow = now.plusMinutes(30);
        OffsetDateTime thirtyFiveMinutesFromNow = now.plusMinutes(35);
        List<Event> upcomingEvents = eventService.getEventsForReminder(now, thirtyFiveMinutesFromNow);
        log.info("Found {} events starting in 30-35 minutes", upcomingEvents.size());
    }

    @Test
    public void test_sendMsgToGroup() {
        String body = "*Test* from JUnit";
        whatsappChannelService.sendMsgToGroup(body);
    }

}
