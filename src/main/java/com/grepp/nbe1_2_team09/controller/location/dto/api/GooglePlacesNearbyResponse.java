package com.grepp.nbe1_2_team09.controller.location.dto.api;

import java.util.List;

public record GooglePlacesNearbyResponse(List<Result> results) {
    public record Result(String place_id, String name, List<Photo> photos) {
        public record Photo(String photo_reference) {
        }
    }
}