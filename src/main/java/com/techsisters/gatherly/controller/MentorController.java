package com.techsisters.gatherly.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techsisters.gatherly.dto.MentorProfileDTO;
import com.techsisters.gatherly.request.CreateMentorProfileRequest;
import com.techsisters.gatherly.request.MentorAvailabilityRequest;
import com.techsisters.gatherly.request.UpdateMentorProfileRequest;
import com.techsisters.gatherly.response.MentorProfileResponse;
import com.techsisters.gatherly.service.MentorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Write endpoints for mentors (login required).
 * The read endpoints anyone can see live in PublicController under /public/mentors.
 *
 * No try/catch here: exceptions thrown by MentorService are turned into
 * error responses by GlobalExceptionHandler.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentors")
public class MentorController {

    private final MentorService mentorService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public MentorProfileResponse createMentorProfile(@Valid @RequestBody CreateMentorProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("MentorController| createMentorProfile| by admin {}", userDetails.getUsername());
        return success(mentorService.createMentorProfile(request), "Mentor profile created successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/me")
    public MentorProfileResponse updateMyProfile(@Valid @RequestBody UpdateMentorProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return success(mentorService.updateMentorProfile(userDetails.getUsername(), request),
                "Profile updated successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/me/availability")
    public MentorProfileResponse setMyAvailability(@Valid @RequestBody MentorAvailabilityRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean available = request.getAvailable();
        return success(mentorService.setAvailability(userDetails.getUsername(), available),
                available ? "You are now available" : "You are now unavailable");
    }

    private MentorProfileResponse success(MentorProfileDTO mentor, String message) {
        MentorProfileResponse response = new MentorProfileResponse();
        response.setMentor(mentor);
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }
}
