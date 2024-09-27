package com.grepp.nbe1_2_team09.domain.repository.group;

import com.grepp.nbe1_2_team09.domain.entity.Group;
import com.grepp.nbe1_2_team09.domain.entity.GroupMembership;
import com.grepp.nbe1_2_team09.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {

    boolean existsByGroupAndUser(Group group, User user);
}
