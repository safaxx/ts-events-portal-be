package com.techsisters.gatherly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.techsisters.gatherly.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findByEventIdIn(List<Long> eventIds);

    List<Event> findByCreatedBy(String createdBy);

}
