package com.grepp.nbe1_2_team09.controller.location.dto;

import com.grepp.nbe1_2_team09.domain.entity.Location;
import com.grepp.nbe1_2_team09.domain.entity.LocationType;

import java.math.BigDecimal;

public record LocationDto(
        Long locationId,
        String placeName,
        BigDecimal latitude,
        BigDecimal longitude,
        String address,
        BigDecimal rating,
        String photo
) {
    public static LocationDto fromEntity(Location location){
        return new LocationDto(
                location.getLocationId(),
                location.getPlaceName(),
                location.getLatitude(),
                location.getLongitude(),
                location.getAddress(),
                location.getRating(),
                location.getPhoto()
        );
    }
}
