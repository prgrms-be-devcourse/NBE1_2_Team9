package com.grepp.nbe1_2_team09.controller.location.dto;

import com.grepp.nbe1_2_team09.domain.entity.Location;
import com.grepp.nbe1_2_team09.domain.entity.LocationType;

import java.math.BigDecimal;

public record LocationEventDto(
        Long locationId,
        String placeName,
        BigDecimal latitude,
        BigDecimal longitude,
        String address,
     /*   String city,
        String country,*/
        BigDecimal rating
) {
    public static LocationEventDto from(Location location) {
        return new LocationEventDto(
                location.getLocationId(),
                location.getPlaceName(),
                location.getLatitude(),
                location.getLongitude(),
                location.getAddress(),
      /*          location.getCity(),
                location.getCountry(),*/
                location.getRating()
        );
    }
}
