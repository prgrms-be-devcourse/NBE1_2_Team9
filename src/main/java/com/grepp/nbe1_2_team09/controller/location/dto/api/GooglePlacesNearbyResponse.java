package com.grepp.nbe1_2_team09.controller.location.dto.api;

import java.math.BigDecimal;
import java.util.List;

public record GooglePlacesNearbyResponse(List<Result> results) {
    public record Result(String place_id, String name, Double rating, Integer user_ratings_total,String vicinity, List<Photo> photos, Geometry geometry) {
    }

    public record Geometry(Location location) {
    }

    public record Location(BigDecimal lat, BigDecimal lng) {
    }

    public record Photo(String photo_reference) {
    }
}
