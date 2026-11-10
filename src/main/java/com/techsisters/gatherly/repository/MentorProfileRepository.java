package com.techsisters.gatherly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.techsisters.gatherly.entity.MentorProfile;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    // Traverses the user relationship: WHERE user.email = ?
    Optional<MentorProfile> findByUserEmail(String email);

    boolean existsByUserEmail(String email);

    // @EntityGraph loads user + specialties in the same query, avoiding N+1 when mapping the list.
    @Override
    @EntityGraph(attributePaths = { "user", "specialties" })
    List<MentorProfile> findAll();

    @EntityGraph(attributePaths = { "user", "specialties" })
    List<MentorProfile> findByAvailableTrue();
}
