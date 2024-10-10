package com.grepp.nbe1_2_team09.domain.repository.event.eventrepo;

import com.grepp.nbe1_2_team09.domain.entity.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

}
