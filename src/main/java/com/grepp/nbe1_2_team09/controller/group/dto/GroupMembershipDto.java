package com.grepp.nbe1_2_team09.controller.group.dto;

import com.grepp.nbe1_2_team09.domain.entity.group.GroupMembership;
import com.grepp.nbe1_2_team09.domain.entity.group.GroupRole;

public record GroupMembershipDto(Long id, Long groupId, String username, GroupRole role) {
    public static GroupMembershipDto from(GroupMembership membership) {
        return new GroupMembershipDto(
                membership.getMembershipId(),
                membership.getGroup().getGroupId(),
                membership.getUser().getUsername(),
                membership.getRole()
        );
    }
}
