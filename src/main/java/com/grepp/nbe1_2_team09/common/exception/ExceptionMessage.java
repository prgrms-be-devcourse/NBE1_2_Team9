package com.grepp.nbe1_2_team09.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    // ADMIN
    INVALID_TOKEN("유효하지 않은 JWT 토큰입니다."),
    EXPIRED_TOKEN("만료된 JWT 토큰입니다."),
    UNSUPPORTED_TOKEN("지원되지 않는 JWT 토큰입니다."),
    EMPTY_CLAIMS("JWT 클레임이 비어있습니다."),

    // USER
    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다."),
    USER_IS_PRESENT("해당 이메일로 가입된 계정이 이미 있습니다."),
    USER_NOT_LOGIN("로그인을 먼저 진행해주세요."),
    USER_LOGIN_FAIL("이메일과 비밀번호를 다시 확인해주세요."),
    ADMIN_ACCESS_ONLY("관리자만 접근할 수 있습니다.")

    ;

    private final String text;
}