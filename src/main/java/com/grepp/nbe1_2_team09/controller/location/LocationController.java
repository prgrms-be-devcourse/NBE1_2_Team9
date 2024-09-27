package com.grepp.nbe1_2_team09.controller.location;

import com.grepp.nbe1_2_team09.controller.city.dto.CityResponse;
import com.grepp.nbe1_2_team09.controller.location.dto.LocationDetailResponse;
import com.grepp.nbe1_2_team09.controller.location.dto.LocationResponse;
import com.grepp.nbe1_2_team09.domain.entity.LocationType;
import com.grepp.nbe1_2_team09.domain.service.location.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/places")
public class LocationController {

    private final LocationService locationService;



    @PostMapping("/searchNearbyPlaces")
    public ResponseEntity<List<LocationResponse>> searchNearbyPlaces(
            @RequestBody CityResponse cityResponse,
            @RequestParam(required = false) LocationType type) {

        List<LocationResponse> places = locationService.searchNearbyPlaces(cityResponse, type);
        return ResponseEntity.ok(places);
    }

    @GetMapping("/searchNearbyPlaces/{placeId}")
    public ResponseEntity<LocationDetailResponse> getLocationDetails(@PathVariable String placeId) {
        LocationDetailResponse details = locationService.getLocationDetails(placeId);
        return ResponseEntity.ok(details);
    }
}
