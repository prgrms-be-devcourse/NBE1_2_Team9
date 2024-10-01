package com.grepp.nbe1_2_team09.controller.location.dto.api;

import java.util.List;

public record GooglePlacesAutocompleteResponse(List<Prediction> predictions) {
    public record Prediction(String place_id, String description) {}
}
