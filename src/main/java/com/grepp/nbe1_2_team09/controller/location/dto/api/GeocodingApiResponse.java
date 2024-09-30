package com.grepp.nbe1_2_team09.controller.location.dto.api;

import java.util.List;

public record GeocodingApiResponse(List<Result> results) {
    public record Result(Geometry geometry) {}
    public record Geometry(Location location) {}
    public record Location(double lat, double lng) {}
}
