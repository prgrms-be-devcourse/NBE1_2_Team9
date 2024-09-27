package com.grepp.nbe1_2_team09.domain.repository.event;

import com.grepp.nbe1_2_team09.domain.entity.EventLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLocationRepository extends JpaRepository<EventLocation, Long> {
}
