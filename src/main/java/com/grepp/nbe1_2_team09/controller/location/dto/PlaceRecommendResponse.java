package com.grepp.nbe1_2_team09.controller.location.dto;

import java.math.BigDecimal;

public record PlaceRecommendResponse(
        String placeId,
        String name,
        BigDecimal latitude,
        BigDecimal longitude,
        double rating,
        Integer user_ratings_total,
        String address,
        String photoUrl
) {
}
