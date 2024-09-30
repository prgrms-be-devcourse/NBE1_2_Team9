package com.grepp.nbe1_2_team09.controller.city;

import com.grepp.nbe1_2_team09.controller.city.dto.SearchCityResponse;
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

    // 도시 또는 나라 검색
    @GetMapping("/search")
    public ResponseEntity<List<SearchCityResponse>> searchCities(@RequestParam(name = "input") String input) {
        List<SearchCityResponse> results = cityService.searchCity(input);
        return ResponseEntity.ok(results);
    }
}
