package com.grepp.nbe1_2_team09.controller.city;

import com.grepp.nbe1_2_team09.controller.city.dto.CityResponse;
import com.grepp.nbe1_2_team09.domain.service.city.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping("/famous")
    public List<CityResponse> getFamousCities(@RequestParam String city) {
        try {
            return cityService.searchCities(city);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // 예외 발생 시 빈 리스트 반환
        }
    }
}