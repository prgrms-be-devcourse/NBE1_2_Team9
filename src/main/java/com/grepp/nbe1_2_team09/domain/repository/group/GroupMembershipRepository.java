package com.grepp.nbe1_2_team09.domain.repository.group;

import com.grepp.nbe1_2_team09.domain.entity.group.Group;
import com.grepp.nbe1_2_team09.domain.entity.group.GroupMembership;
import com.grepp.nbe1_2_team09.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.user.userId = :userId")
    List<GroupMembership> findByUser_Id(Long userId);
    List<GroupMembership> findByGroup(Group group);
    boolean existsByGroupAndUser(Group group, User user);
    Optional<GroupMembership> findByGroupAndUser(Group group, User user);

}
