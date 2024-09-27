package com.grepp.nbe1_2_team09.domain.repository.event;

import com.grepp.nbe1_2_team09.domain.entity.Event;
import com.grepp.nbe1_2_team09.domain.entity.EventLocation;
import com.grepp.nbe1_2_team09.domain.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventLocationRepository extends JpaRepository<EventLocation, Long> {
    List<EventLocation> findByEvent(Event event);

    Optional<EventLocation> findByEventAndLocation(Event event, Location location);
}
