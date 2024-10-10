package com.grepp.nbe1_2_team09.domain.repository.group.membership;

import com.grepp.nbe1_2_team09.domain.entity.group.Group;
import com.grepp.nbe1_2_team09.domain.entity.group.GroupMembership;
import com.grepp.nbe1_2_team09.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long>, GroupMembershipRepositoryCustom {
    boolean existsByGroupAndUser(Group group, User user);


}
