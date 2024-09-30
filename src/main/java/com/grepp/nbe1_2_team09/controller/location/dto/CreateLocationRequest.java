package com.grepp.nbe1_2_team09.controller.location.dto;

import com.grepp.nbe1_2_team09.domain.entity.Location;
import com.grepp.nbe1_2_team09.domain.entity.LocationType;

import java.math.BigDecimal;

public record CreateLocationRequest(
         String placeName,
         BigDecimal latitude,
         BigDecimal longitude,
         String address,
         BigDecimal rating,
         LocationType type,
         String photo) {

    //DTO->Entity
    public Location toEntity(){
        return Location.builder()
                .placeName(placeName)
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .rating(rating)
                .type(type)
                .photo(photo)
                .build();

    }
}
