package com.grepp.nbe1_2_team09.controller.location.dto;

import java.math.BigDecimal;
import java.util.List;

public record PlaceDetailResponse(
        String place_id,
        String name,
        BigDecimal latitude,
        BigDecimal longitude,
        String formatted_address,
        String formatted_phone_number,
        String photoUrl,
        double rating,
        String url,
        String weekday_text,  // 요일 정보
        String website,
        boolean open_now  // 현재 영업 여부
) {
}

