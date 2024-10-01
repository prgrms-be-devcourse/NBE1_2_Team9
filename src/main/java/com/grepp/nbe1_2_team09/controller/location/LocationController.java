package com.grepp.nbe1_2_team09.controller.location;

import com.grepp.nbe1_2_team09.controller.location.dto.CreateLocationRequest;
import com.grepp.nbe1_2_team09.controller.location.dto.LocationDto;
import com.grepp.nbe1_2_team09.domain.service.location.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    // 장소 저장
    @PostMapping
    public ResponseEntity<LocationDto> createLocation(@RequestBody CreateLocationRequest locationReq) {
        LocationDto createdLocation = locationService.saveLocation(locationReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation);
    }

    // 장소 조회
    @GetMapping("/{locationId}")
    public ResponseEntity<LocationDto> getLocation(@PathVariable Long locationId) {
        LocationDto location = locationService.getLocationById(locationId);
        return ResponseEntity.ok(location);
    }

    // 장소 삭제
    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long locationId) {
        locationService.deleteLocationById(locationId);
        return ResponseEntity.noContent().build();
    }

}
