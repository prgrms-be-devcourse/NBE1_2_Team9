package com.grepp.nbe1_2_team09.controller.location;

import com.grepp.nbe1_2_team09.controller.location.dto.PlaceDetailResponse;
import com.grepp.nbe1_2_team09.controller.location.dto.PlaceRecommendResponse;
import com.grepp.nbe1_2_team09.controller.location.dto.PlaceResponse;
import com.grepp.nbe1_2_team09.domain.service.location.LocationApiService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/locations")
public class LocationApiController {


    private final LocationApiService placeService;

    //장소 자동 검색
    @GetMapping("/{eventId}/autocomplete")
    public ResponseEntity<List<PlaceResponse>> autocomplete(
            @PathVariable(name="eventId") Long eventId,
            @RequestParam(name = "input") String input) {
        List<PlaceResponse> places = placeService.getAutocompletePlaces(eventId, input);
        return ResponseEntity.ok(places);
    }


    // 추천 장소 검색
    @GetMapping("/{eventId}/recommend")
    public ResponseEntity<List<PlaceRecommendResponse>> getRecommendedPlaces(
            @PathVariable(name="eventId") Long eventId,
            @RequestParam(name = "type", defaultValue = "establishment") String type) {
        List<PlaceRecommendResponse> places = placeService.getRecommendedPlaces(eventId, type);
        return ResponseEntity.ok(places);
    }

    //장소 상세 정보 조회
    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceDetailResponse> getPlaceDetail(@PathVariable(name = "placeId") String placeId) { // name 속성 추가
        PlaceDetailResponse placeDetail = placeService.getPlaceDetail(placeId);
        return ResponseEntity.ok(placeDetail);
    }
}

