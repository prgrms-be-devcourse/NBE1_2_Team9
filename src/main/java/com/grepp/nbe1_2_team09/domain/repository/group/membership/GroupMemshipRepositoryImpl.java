package com.grepp.nbe1_2_team09.domain.repository.group.membership;

import com.grepp.nbe1_2_team09.domain.entity.group.Group;
import com.grepp.nbe1_2_team09.domain.entity.group.GroupMembership;
import com.grepp.nbe1_2_team09.domain.entity.group.QGroup;
import com.grepp.nbe1_2_team09.domain.entity.user.QUser;
import com.grepp.nbe1_2_team09.domain.entity.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.grepp.nbe1_2_team09.domain.entity.group.QGroupMembership.groupMembership;
import static com.grepp.nbe1_2_team09.domain.entity.user.QUser.user;

public class GroupMemshipRepositoryImpl implements GroupMembershipRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public GroupMemshipRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<GroupMembership> findByGroupAndUser(Group group, User user) {

        QUser qUser = QUser.user;

        GroupMembership result = queryFactory
                .selectFrom(groupMembership)
                .leftJoin(groupMembership.user, qUser).fetchJoin()
                .where(groupMembership.group.eq(group)
                        .and(groupMembership.user.eq(user)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<GroupMembership> findByUserId(Long userId) {


        return queryFactory
                .selectFrom(groupMembership)
                .leftJoin(groupMembership.user, user).fetchJoin()
                .where(groupMembership.user.userId.eq(userId))
                .fetch();  // 결과 리스트로 반환
    }

    @Override
    public List<GroupMembership> findByGroup(Group group) {

        QGroup qGroup = QGroup.group;

        return queryFactory
                .selectFrom(groupMembership)
                .leftJoin(groupMembership.group, qGroup).fetchJoin()  // N+1 문제 해결
                .where(groupMembership.group.eq(group))               // group 조건 필터링
                .fetch();  // 결과 리스트로 반환
    }
}
