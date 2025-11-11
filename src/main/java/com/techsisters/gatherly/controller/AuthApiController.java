package com.techsisters.gatherly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techsisters.gatherly.dto.ResponseDTO;
import com.techsisters.gatherly.request.OtpRequest;
import com.techsisters.gatherly.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth/")
public class AuthApiController {

    @Autowired
    private UserService userService;

    @PostMapping("/send-otp")
    public ResponseDTO generateOTP(@RequestBody OtpRequest request) {
        log.info("Received request to generate OTP for email: {}", request.getEmail());
        ResponseDTO response = new ResponseDTO();

        try {
            Integer otp = userService.generateOTP(request.getEmail());
            // TODO: send email notification with OTP
            response.setSuccess(true);
            response.setMessage("A 6-digit OTP has been sent to " + request.getEmail() + ".");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error while generating OTP: " + e.getMessage());
        }
        return response;
    }

}
