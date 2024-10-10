package com.grepp.nbe1_2_team09.domain.repository.event.eventrepo;

import com.grepp.nbe1_2_team09.domain.entity.event.Event;
import com.grepp.nbe1_2_team09.domain.entity.group.Group;
import com.grepp.nbe1_2_team09.domain.entity.group.QGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.grepp.nbe1_2_team09.domain.entity.event.QEvent.event;

public class EventRepositoryImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public EventRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Event> findByGroup(Group group) {

        QGroup qGroup = QGroup.group;

        return queryFactory
                .selectFrom(event)
                .leftJoin(event.group, qGroup).fetchJoin()
                .where(event.group.eq(group))
                .fetch();

    }
}
