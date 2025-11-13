package com.techsisters.gatherly.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.techsisters.gatherly.entity.User;
import com.techsisters.gatherly.integration.airtable.AirtableService;
import com.techsisters.gatherly.integration.airtable.response.Record;
import com.techsisters.gatherly.repository.UserRepository;
import com.techsisters.gatherly.request.LoginRequest;
import com.techsisters.gatherly.response.LoginResponse;
import com.techsisters.gatherly.util.UserUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AirtableService airtableService;
    private final EmailService emailService;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public LoginResponse authenticateUser(LoginRequest request) {
        LoginResponse response = new LoginResponse();

        try {
            validateOTP(request.getEmail(), request.getOtp());

            User user = findByEmail(request.getEmail());

            // clear OTP after successful validation
            user.setOtp(Integer.valueOf(0));
            user.setOtpCreatedDate(null);
            userRepository.save(user);

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            // Generate the JWT
            String jwtToken = jwtService.generateToken(userDetails.getUsername());

            response.setSuccess(true);
            response.setMessage("Login successful.");
            response.setAccessToken(jwtToken);
            response.setName(user.getName());
            response.setEmail(userDetails.getUsername());
            response.setCountry(user.getCountry());

            log.info("Login successful for user: {}", request.getEmail());

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error during authentication: " + e.getMessage());
        }
        return response;
    }

    public void validateOTP(String email, Integer otp) throws Exception {
        User user = findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User with email " + email + " not found");
        }

        if (user.getOtp() == 0 || !otp.equals(user.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        Date otpCreatedDate = user.getOtpCreatedDate();
        Date currentDate = new Date();

        long timeDiff = currentDate.getTime() - otpCreatedDate.getTime();
        long timeDiffInMinutes = TimeUnit.MINUTES.convert(timeDiff, TimeUnit.MILLISECONDS);

        // OTP is valid for 10 minutes
        if (timeDiffInMinutes > 10) {
            throw new IllegalArgumentException("OTP has expired");
        }

    }

    /*
     * Check if user is a registered Techsisters member via Airtable
     * if valid generate 6-digit OTP and send to email
     */
    public Integer generateOTP(String email) throws Exception {
        log.info("Checking if user with email {} is a Techsisters member", email);

        Integer otp = null;

        User user = findByEmail(email);
        if (user == null) {
            Record userRecord = airtableService.findByEmail(email);

            if (userRecord != null) {
                log.info("User with email {} is a Techsisters member", email);

                // create user in DB
                user = new User();

                user = copyAirtableData(user, userRecord);
                log.info("User data saved/updated in DB: {}", user.getId());

            } else {
                log.info("User with email {} is not a Techsisters member", email);
                throw new IllegalArgumentException("User " + email + " is not a Techsisters member");
            }

        } else {
            log.info("User with email {} already exists in DB", email);
        }

        // generate OTP
        otp = UserUtil.generate6DigitCode();
        user.setOtp(otp);
        user.setOtpCreatedDate(new Date());
        userRepository.save(user);

        // send OTP email
        emailService.sendOtpEmail(email, user.getName(), String.valueOf(otp));

        return otp;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User copyAirtableData(User user, Record record) {
        user.setName(record.getFields().getName());
        user.setEmail(record.getFields().getEmail());
        user.setCountry(record.getFields().getCountry());

        return userRepository.save(user);
    }

}
