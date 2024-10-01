package com.grepp.nbe1_2_team09.controller.city.dto;

public record SearchCityResponse(
        String placeId ,   // Google Places API에서의 placeId
        String description      // 도시 이름

) {}
