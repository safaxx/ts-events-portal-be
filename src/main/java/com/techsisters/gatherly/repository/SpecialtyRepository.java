package com.techsisters.gatherly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techsisters.gatherly.entity.Specialties;

public interface SpecialtyRepository extends JpaRepository<Specialties, Long> {

    List<Specialties> findAllByOrderByNameAsc();
}
