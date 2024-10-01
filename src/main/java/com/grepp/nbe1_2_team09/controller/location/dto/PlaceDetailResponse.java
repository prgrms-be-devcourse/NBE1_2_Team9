package com.grepp.nbe1_2_team09.controller.location.dto;

import java.util.List;

public record PlaceDetailResponse(
        String place_id,
        String name,
        String formatted_address,
        String formatted_phone_number,
        Photo photo,
        double rating,
        String url
) {
    public record Photo(String photo_reference) {}
}

