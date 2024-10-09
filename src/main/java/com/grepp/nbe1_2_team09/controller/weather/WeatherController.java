package com.grepp.nbe1_2_team09.controller.weather;

import com.grepp.nbe1_2_team09.domain.service.weather.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/forecast")
    public String getForecast(@RequestParam String city) {
        return weatherService.getForecastByCityName(city);
    }

}
