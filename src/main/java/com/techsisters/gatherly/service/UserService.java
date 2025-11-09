package com.techsisters.gatherly.service;

import org.springframework.stereotype.Service;

import com.techsisters.gatherly.entity.User;
import com.techsisters.gatherly.integration.airtable.AirtableService;
import com.techsisters.gatherly.integration.airtable.response.Record;
import com.techsisters.gatherly.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AirtableService airtableService;

    /*
     * Check if user is a registered Techsisters member via Airtable
     * if valid generate 6-digit OTP and send to email
     */
    public Integer generateOTP(String email) throws Exception {
        log.info("Checking if user with email {} is a Techsisters member", email);

        Integer otp = null;

        Record userRecord = airtableService.findByEmail(email);
        if (userRecord != null) {
            log.info("User with email {} is a Techsisters member", email);

            // create/update user in DB
            User user = findByEmail(email);
            if (user == null) {
                user = new User();
            }
            user = copyAirtableData(user, userRecord);

        } else {
            log.info("User with email {} is not a Techsisters member", email);
            throw new IllegalArgumentException("User " + email + " is not a Techsisters member");
        }

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
