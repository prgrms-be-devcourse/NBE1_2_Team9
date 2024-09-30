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

/*String placeId,
        String name,
        String photoUrl,
        String address,
        String phoneNumber,
        String website,
        double rating,
        int userRatingsTotal,
        double latitude,  // 위도
        double longitude   // 경도*/