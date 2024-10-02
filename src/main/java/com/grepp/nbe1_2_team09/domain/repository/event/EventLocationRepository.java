package com.grepp.nbe1_2_team09.domain.repository.event;

import com.grepp.nbe1_2_team09.domain.entity.event.Event;
import com.grepp.nbe1_2_team09.domain.entity.event.EventLocation;
import com.grepp.nbe1_2_team09.domain.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventLocationRepository extends JpaRepository<EventLocation, Long> {
    List<EventLocation> findByEvent(Event event);

    @Query("SELECT el from EventLocation el WHERE el.event=:event AND DATE(el.visitStartTime) =:date ORDER BY el.visitStartTime ASC")
    List<EventLocation> findByEventAndDateOrderByTime(Event event, LocalDate date);

    Optional<EventLocation> findByEventAndLocation(Event event, Location location);
}
