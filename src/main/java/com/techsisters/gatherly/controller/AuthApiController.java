package com.techsisters.gatherly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techsisters.gatherly.dto.ResponseDTO;
import com.techsisters.gatherly.request.LoginRequest;
import com.techsisters.gatherly.request.OtpRequest;
import com.techsisters.gatherly.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth/")
public class AuthApiController {

    @Autowired
    private UserService userService;

    @PostMapping("/send-otp")
    public ResponseDTO generateOTP(@Valid @RequestBody OtpRequest request) {
        log.info("Received request to generate OTP for email: {}", request.getEmail());
        ResponseDTO response = new ResponseDTO();

        try {
            userService.generateOTP(request.getEmail());
            response.setSuccess(true);
            response.setMessage("A 6-digit OTP has been sent to " + request.getEmail() + ".");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error while generating OTP: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/login")
    public ResponseDTO login(@Valid @RequestBody LoginRequest request) {
        log.info("Attempting to login with email: {}", request.getEmail());
        ResponseDTO response = new ResponseDTO();
        try {
            response = userService.authenticateUser(request);
        } catch (Exception e) {
            log.error("Login failed for email {}: {}", request.getEmail(), e.getMessage());
            response.setSuccess(false);
            response.setMessage("Login failed: " + e.getMessage());
        }

        return response;
    }

}
