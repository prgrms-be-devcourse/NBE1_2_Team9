package com.grepp.nbe1_2_team09.controller.event.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AddEventLocationReq(
        @NotNull(message = "위치 ID 필수")
        Long locationId,

        @Size(max = 500, message = "설명은 초대 500자")
        String description,

        @NotNull(message = "방문 시작 시간 필수")
        LocalDateTime visitStartTime,

        @NotNull(message = "방문 종료 시간 필수")
        LocalDateTime visitEndTime

) {
    @AssertTrue(message = "방문 종료 시간은 시작 시간 이후여야 합니다.")
    public boolean isValidTimeRange() {
        return visitStartTime != null && visitEndTime != null && !visitEndTime.isBefore(visitStartTime);
    }
}
