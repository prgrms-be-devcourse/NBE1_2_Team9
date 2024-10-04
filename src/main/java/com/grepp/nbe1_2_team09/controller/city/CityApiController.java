package com.grepp.nbe1_2_team09.controller.city;

import com.grepp.nbe1_2_team09.controller.city.dto.CityResponse;
import com.grepp.nbe1_2_team09.domain.service.city.CityApiService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cities")
public class CityApiController {

    private final CityApiService cityService;

    // 도시
    @GetMapping("/search")
    public ResponseEntity<List<CityResponse>> searchCities(@RequestParam(name = "input") String input) {
        List<CityResponse> results = cityService.searchCity(input);
        return ResponseEntity.ok(results);
    }

    //나라별 도시 검색
    @GetMapping("/country")
    public ResponseEntity<List<CityResponse>> getCitiesByCountry(@RequestParam String input) {
        List<CityResponse> cities = cityService.getCitiesByCountry(input);
        return ResponseEntity.ok(cities);
    }
}
