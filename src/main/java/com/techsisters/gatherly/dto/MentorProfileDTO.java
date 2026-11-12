package com.techsisters.gatherly.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentorProfileDTO {

    private Long mentorId;
    private String name;
    private String headline;
    private String bio;
    // Only populated when available == true (see MentorMapper).
    private String meetingLink;
    private boolean available;
    // id + name, so the frontend can show names and pre-select ids in the edit form.
    private List<SpecialtyDTO> specialties = new ArrayList<>();
}
