package com.grepp.nbe1_2_team09.domain.repository.group.membership;

import com.grepp.nbe1_2_team09.domain.entity.group.Group;
import com.grepp.nbe1_2_team09.domain.entity.group.GroupMembership;
import com.grepp.nbe1_2_team09.domain.entity.user.User;

import java.util.List;
import java.util.Optional;

public interface GroupMembershipRepositoryCustom {
    Optional<GroupMembership> findByGroupAndUser(Group group, User user);
    List<GroupMembership> findByUserId(Long userId);
    List<GroupMembership> findByGroup(Group group);

}
