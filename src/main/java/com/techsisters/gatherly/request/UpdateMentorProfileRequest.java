package com.techsisters.gatherly.request;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMentorProfileRequest {

    @NotBlank(message = "Headline is required")
    @Size(max = 255, message = "Headline must be at most 255 characters")
    private String headline;

    @Size(max = 5000, message = "Bio must be at most 5000 characters")
    private String bio;

    // Rendered as a clickable Join button, so only https links are allowed (blocks javascript: etc.).
    @URL(protocol = "https", message = "Meeting link must be a valid https URL")
    @Size(max = 255, message = "Meeting link must be at most 255 characters")
    private String meetingLink;

    private Set<Long> specialtyIds = new HashSet<>();
}
