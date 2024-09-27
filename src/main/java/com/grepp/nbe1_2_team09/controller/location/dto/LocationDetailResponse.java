package com.grepp.nbe1_2_team09.controller.location.dto;

import com.grepp.nbe1_2_team09.domain.entity.LocationType;

import java.util.List;

public record LocationDetailResponse(
        String placeId,
        String name,
        String photoUrl,
        String address,
        String phoneNumber,
        String website,
        double rating,
        int userRatingsTotal,
        double latitude,  // 위도
        double longitude   // 경도
) { }
