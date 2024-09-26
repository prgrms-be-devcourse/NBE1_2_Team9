package com.grepp.nbe1_2_team09.controller.group.dto;

import com.grepp.nbe1_2_team09.domain.entity.Group;

import java.time.LocalDateTime;

public record GroupDto(Long groupId, String groupName, LocalDateTime creationDate) {
    public static GroupDto from(Group group){
        return new GroupDto(group.getGroupId(), group.getGroupName(), group.getCreationDate());
    }
}
