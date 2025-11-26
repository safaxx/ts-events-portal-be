package com.techsisters.gatherly.repository;

import com.techsisters.gatherly.entity.Event;
import com.techsisters.gatherly.entity.EventRSVP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRSVPRepository extends JpaRepository<EventRSVP, Long> {
    Optional<EventRSVP> findByEventAndUserEmail(Event event, String userEmail);

    List<EventRSVP> findAllByEvent_EventId(Long eventId);

    List<EventRSVP> findByUserEmailAndRsvpStatus(String userEmail, boolean rsvpStatus);

    //EventRSVP findByEventAndUserEmail(Event event, String userEmail);

//    @Query("SELECT e FROM Event e WHERE e.id IN :eventIds")
//    List<Event> findByIds(@Param("eventIds") List<Long> eventIds);

}

