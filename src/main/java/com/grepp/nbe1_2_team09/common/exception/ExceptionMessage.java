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
    ADMIN_ACCESS_ONLY("관리자만 접근할 수 있습니다."),

    //AccountBook
    GROUP_NOT_FOUND("그룹을 찾을 수 없습니다"),
    MEMBER_ACCESS_ONLY("리소스에 대한 액세스 권한이 없습니다. 해당 유저가 그룹에 속해있지 않습니다."),
    DB_ERROR("저장 실패"),
    EXPENSE_NOT_FOUND("해당 지출 목록을 찾을 수 없습니다."),
    OCR_ERROR("이미지 추출 오류"),
    IMAGE_NOT_FOUND("이미지가 존재하지 않음")


    ;

    private final String text;
}