package com.grepp.nbe1_2_team09.domain.entity.group;

import lombok.Getter;

@Getter
public enum GroupRole {
    OWNER(3),
    ADMIN(2),
    MEMBER(1);

    private final int priority;

    GroupRole(int priority) {
        this.priority = priority;
    }

}
