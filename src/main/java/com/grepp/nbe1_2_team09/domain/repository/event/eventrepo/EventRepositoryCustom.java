package com.grepp.nbe1_2_team09.domain.repository.event.eventrepo;

import com.grepp.nbe1_2_team09.domain.entity.event.Event;
import com.grepp.nbe1_2_team09.domain.entity.group.Group;

import java.util.List;

public interface EventRepositoryCustom {
    List<Event> findByGroup(Group group);
}
