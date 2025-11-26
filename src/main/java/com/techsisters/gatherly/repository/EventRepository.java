package com.techsisters.gatherly.repository;

import com.techsisters.gatherly.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByEventIdIn(List<Long> eventIds);
    List<Event> findByCreatedBy(String createdBy);
}
