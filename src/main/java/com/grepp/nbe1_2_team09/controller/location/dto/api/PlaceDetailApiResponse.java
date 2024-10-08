package com.grepp.nbe1_2_team09.controller.location.dto.api;

import com.grepp.nbe1_2_team09.controller.location.dto.PlaceDetailResponse;

import java.math.BigDecimal;
import java.util.List;

public record PlaceDetailApiResponse(Result result) {
    public record Result(
            String place_id,
            String name,
            Geometry geometry,
            String formatted_address,
            String formatted_phone_number,
            List<Photo> photos,
            double rating,
            String url,
            String website,
            OpeningHours current_opening_hours
    ){

    }
    public record Geometry(GooglePlacesNearbyResponse.Location location) {
    }

    public record Location(BigDecimal lat, BigDecimal lng) {
    }
    public record Photo(String photo_reference) {
    }
    public record OpeningHours(
            boolean open_now,       // 현재 영업 여부
            List<String> weekday_text // 요일별 영업 시간 정보
    ) {
    }
}
