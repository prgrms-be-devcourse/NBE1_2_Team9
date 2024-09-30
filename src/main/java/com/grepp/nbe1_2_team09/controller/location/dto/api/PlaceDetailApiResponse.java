package com.grepp.nbe1_2_team09.controller.location.dto.api;

import com.grepp.nbe1_2_team09.controller.location.dto.PlaceDetailResponse;

import java.util.List;

public record PlaceDetailApiResponse(Result result) {
    public record Result(
            String place_id,
            String name,
            String formatted_address,
            String formatted_phone_number,
            Photo photos,
            double rating,
            String url
    ) {
        public record Photo(String photo_reference) {}
    }
}
