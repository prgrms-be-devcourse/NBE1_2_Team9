package com.grepp.nbe1_2_team09.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    // User
    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다."),

    // Group
    GROUP_NOT_FOUND("해당 그룹을 찾을 수 없습니다."),
    USER_ALREADY_IN_GROUP("해당 사용자는 이미 그룹의 멤버입니다."),
    USER_NOT_IN_GROUP("해당 사용자는 그룹의 멤버가 아닙니다."),
    CANNOT_REMOVE_LAST_ADMIN("그룹에는 최소 한 명의 관리자가 필요합니다."),

    // Event
    EVENT_NOT_FOUND("해당 이벤트를 찾을 수 없습니다."),
    EVENT_DATE_INVALID("이벤트 시작 시간은 종료 시간보다 늦을 수 없습니다.");


    private final String text;
}