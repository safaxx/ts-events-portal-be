package com.techsisters.gatherly.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techsisters.gatherly.dto.MentorProfileDTO;
import com.techsisters.gatherly.dto.SpecialtyDTO;
import com.techsisters.gatherly.entity.MentorProfile;
import com.techsisters.gatherly.entity.Specialties;
import com.techsisters.gatherly.entity.User;
import com.techsisters.gatherly.exception.ResourceNotFoundException;
import com.techsisters.gatherly.mapper.MentorMapper;
import com.techsisters.gatherly.repository.MentorProfileRepository;
import com.techsisters.gatherly.repository.SpecialtyRepository;
import com.techsisters.gatherly.repository.UserRepository;
import com.techsisters.gatherly.request.CreateMentorProfileRequest;
import com.techsisters.gatherly.request.UpdateMentorProfileRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MentorService {

    private final MentorProfileRepository mentorProfileRepository;
    private final SpecialtyRepository specialtyRepository;
    private final UserRepository userRepository;
    private final MentorMapper mentorMapper;

    // viewerLoggedIn: meeting links are only shown to logged-in members.
    @Transactional(readOnly = true)
    public List<MentorProfileDTO> getAllMentors(boolean availableOnly, boolean viewerLoggedIn) {
        List<MentorProfile> mentors = availableOnly
                ? mentorProfileRepository.findByAvailableTrue()
                : mentorProfileRepository.findAll();
        return mentorMapper.getMentorProfileDTOs(mentors, viewerLoggedIn);
    }

    @Transactional(readOnly = true)
    public MentorProfileDTO getMentorProfileDetails(Long mentorId, boolean viewerLoggedIn) {
        MentorProfile mentor = mentorProfileRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor profile not found"));
        return mentorMapper.getMentorProfileDTO(mentor, viewerLoggedIn);
    }

    @Transactional(readOnly = true)
    public List<SpecialtyDTO> getAllSpecialties() {
        return specialtyRepository.findAllByOrderByNameAsc().stream()
                .map(mentorMapper::getSpecialtyDTO)
                .toList();
    }

    @Transactional
    public MentorProfileDTO createMentorProfile(CreateMentorProfileRequest request) {
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (mentorProfileRepository.existsByUserEmail(request.getUserEmail())) {
            throw new IllegalStateException("This user already has a mentor profile");
        }

        MentorProfile mentor = new MentorProfile();
        mentor.setUser(user);
        applyEditableFields(mentor, request);

        MentorProfile saved = mentorProfileRepository.save(mentor);
        log.info("MentorService| createMentorProfile| created mentorId {} for {}", saved.getMentorId(),
                request.getUserEmail());
        return mentorMapper.getMentorProfileDTO(saved);
    }

    /**
     * The mentor is identified by the logged-in user's email (from the JWT),
     * never by an id sent from the client.
     */
    @Transactional
    public MentorProfileDTO updateMentorProfile(String email, UpdateMentorProfileRequest request) {
        MentorProfile mentor = getMentorByEmail(email);

        if (mentor.isAvailable() && StringUtils.isBlank(request.getMeetingLink())) {
            throw new IllegalStateException("Turn off availability before removing your meeting link");
        }

        applyEditableFields(mentor, request);
        // No save() needed: the entity is managed inside this transaction, so
        // Hibernate's dirty checking writes the changes on commit.
        return mentorMapper.getMentorProfileDTO(mentor);
    }

    @Transactional
    public MentorProfileDTO setAvailability(String email, boolean available) {
        MentorProfile mentor = getMentorByEmail(email);

        if (available && StringUtils.isBlank(mentor.getMeetingLink())) {
            throw new IllegalStateException("Add a meeting link to your profile before going available");
        }

        mentor.setAvailable(available);
        return mentorMapper.getMentorProfileDTO(mentor);
    }

    private MentorProfile getMentorByEmail(String email) {
        return mentorProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("You don't have a mentor profile"));
    }

    private void applyEditableFields(MentorProfile mentor, UpdateMentorProfileRequest request) {
        mentor.setHeadline(StringUtils.trimToNull(request.getHeadline()));
        mentor.setBio(StringUtils.trimToNull(request.getBio()));
        mentor.setMeetingLink(StringUtils.trimToNull(request.getMeetingLink()));
        mentor.setSpecialties(resolveSpecialties(request.getSpecialtyIds()));
    }

    private Set<Specialties> resolveSpecialties(Set<Long> specialtyIds) {
        if (specialtyIds == null || specialtyIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Specialties> found = specialtyRepository.findAllById(specialtyIds);
        if (found.size() != specialtyIds.size()) {
            throw new IllegalArgumentException("One or more specialties do not exist");
        }
        return new HashSet<>(found);
    }
}
