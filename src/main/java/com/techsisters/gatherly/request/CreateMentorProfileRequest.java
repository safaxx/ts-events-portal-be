package com.techsisters.gatherly.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Admin-only: same editable fields as an update, plus which user becomes the mentor.
 */
@Getter
@Setter
public class CreateMentorProfileRequest extends UpdateMentorProfileRequest {

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;
}
