package com.techsisters.gatherly.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestJwtService {

    @Autowired
    private JwtService jwtService;

    @Test
    public void test_generateToken() {
        String token = jwtService.generateToken("noorsuho@gmail.com");
        log.info("Token: {}", token);
    }

}
