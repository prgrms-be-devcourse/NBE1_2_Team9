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
    UNAUTHORIZED_ACTION("사용자가 이 작업을 수행할 권한이 없습니다."),

    // USER
    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다."),
    USER_IS_PRESENT("해당 이메일로 가입된 계정이 이미 있습니다."),
    USER_NOT_LOGIN("로그인을 먼저 진행해주세요."),
    USER_LOGIN_FAIL("이메일과 비밀번호를 다시 확인해주세요."),
    ADMIN_ACCESS_ONLY("관리자만 접근할 수 있습니다."),

    //AccountBook
    MEMBER_ACCESS_ONLY("리소스에 대한 액세스 권한이 없습니다. 해당 유저가 그룹에 속해있지 않습니다."),
    DB_ERROR("저장 실패"),
    EXPENSE_NOT_FOUND("해당 지출 목록을 찾을 수 없습니다."),
    OCR_ERROR("이미지 추출 오류"),
    IMAGE_NOT_FOUND("이미지가 존재하지 않음"),
    FORMAT_ERROR("문자열 포맷팅 실패"),

    //Exchange
    EXCHANGE_ERROR("환전하는 중 오류 발생"),

    // Group
    GROUP_NOT_FOUND("해당 그룹을 찾을 수 없습니다."),
    USER_ALREADY_IN_GROUP("해당 사용자는 이미 그룹의 멤버입니다."),
    USER_NOT_IN_GROUP("해당 사용자는 그룹의 멤버가 아닙니다."),
    CANNOT_REMOVE_LAST_ADMIN("그룹에는 최소 한 명의 관리자가 필요합니다."),
    GROUP_OWNER_ACCESS_ONLY("그룹 소유자만 접근할 수 있습니다."),
    GROUP_ADMIN_ACCESS_ONLY("그룹 관리자만 접근할 수 있습니다."),
    GROUP_DELETION_FAILED("그룹 삭제에 실패했습니다."),
    INSUFFICIENT_PERMISSION("자신보다 낮은 역할의 멤버만 접근할 수 있습니다."),

    // Event
    EVENT_NOT_FOUND("해당 이벤트를 찾을 수 없습니다."),
    EVENT_DATE_INVALID("이벤트 시작 시간은 종료 시간보다 늦을 수 없습니다."),

    //Invitation
    NOT_INVITED_USER("초대받은 유저가 아닙니다."),
    INVITATION_NOT_FOUND("해당 초대를 찾을 수 없습니다."),

    // Location
    LOCATION_NOT_FOUND("해당 장소를 찾을 수 없습니다.")

    ;

    private final String text;
}
