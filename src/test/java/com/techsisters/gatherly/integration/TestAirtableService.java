package com.techsisters.gatherly.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestAirtableService {

    @Autowired
    private AirtableService airtableService;

    @Test
    public void test_getList() {
        Object response = airtableService.getList();
        log.info("Airtable Response: {}", response);
    }

}
