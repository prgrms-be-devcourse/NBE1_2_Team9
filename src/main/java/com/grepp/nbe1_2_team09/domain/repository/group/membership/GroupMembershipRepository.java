package com.grepp.nbe1_2_team09.domain.repository.group.membership;

import com.grepp.nbe1_2_team09.domain.entity.group.Group;
import com.grepp.nbe1_2_team09.domain.entity.group.GroupMembership;
import com.grepp.nbe1_2_team09.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long>, GroupMembershipRepositoryCustom {
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.user.userId = :userId")
    List<GroupMembership> findByUserId(Long userId);
    List<GroupMembership> findByGroup(Group group);
    boolean existsByGroupAndUser(Group group, User user);


}
