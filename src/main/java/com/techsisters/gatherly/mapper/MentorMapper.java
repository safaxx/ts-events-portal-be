package com.techsisters.gatherly.mapper;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.techsisters.gatherly.dto.MentorProfileDTO;
import com.techsisters.gatherly.dto.SpecialtyDTO;
import com.techsisters.gatherly.entity.MentorProfile;
import com.techsisters.gatherly.entity.Specialties;

@Component
public class MentorMapper {

    /** For the mentor or admin who owns/created the profile: link shown when available. */
    public MentorProfileDTO getMentorProfileDTO(MentorProfile mentorProfile) {
        return getMentorProfileDTO(mentorProfile, true);
    }

    /**
     * @param viewerLoggedIn the meeting link is only included for logged-in
     *                       members, and only while the mentor is available.
     */
    public MentorProfileDTO getMentorProfileDTO(MentorProfile mentorProfile, boolean viewerLoggedIn) {
        MentorProfileDTO dto = new MentorProfileDTO();
        dto.setMentorId(mentorProfile.getMentorId());
        dto.setName(mentorProfile.getUser().getName());
        dto.setHeadline(mentorProfile.getHeadline());
        dto.setBio(mentorProfile.getBio());
        dto.setAvailable(mentorProfile.isAvailable());
        if (viewerLoggedIn && mentorProfile.isAvailable()) {
            dto.setMeetingLink(mentorProfile.getMeetingLink());
        }
        dto.setSpecialties(mentorProfile.getSpecialties().stream()
                .map(this::getSpecialtyDTO)
                .sorted(Comparator.comparing(SpecialtyDTO::getName))
                .toList());
        return dto;
    }

    public List<MentorProfileDTO> getMentorProfileDTOs(List<MentorProfile> mentorProfiles, boolean viewerLoggedIn) {
        return mentorProfiles.stream().map(m -> getMentorProfileDTO(m, viewerLoggedIn)).toList();
    }

    public SpecialtyDTO getSpecialtyDTO(Specialties specialty) {
        return new SpecialtyDTO(specialty.getSpecialtyId(), specialty.getName());
    }
}
