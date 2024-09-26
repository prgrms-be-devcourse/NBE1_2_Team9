package com.grepp.nbe1_2_team09.domain.repository.group;

import com.grepp.nbe1_2_team09.domain.entity.Group;
import com.grepp.nbe1_2_team09.domain.entity.GroupMembership;
import com.grepp.nbe1_2_team09.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {

    List<GroupMembership> findByUserId(Long userId);
    List<GroupMembership> findByGroup(Group group);
    boolean existsByGroupAndUser(Group group, User user);
    Optional<GroupMembership> findByGroupAndUser(Group group, User user);

}
