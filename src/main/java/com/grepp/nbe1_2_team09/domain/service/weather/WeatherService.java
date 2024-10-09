package com.grepp.nbe1_2_team09.domain.service.weather;

import com.grepp.nbe1_2_team09.controller.weather.dto.GeocodeRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    // 도시 이름으로 날씨 예보 조회
    public String getForecastByCityName(String city) {
        // 1. Geocoding API (위도와 경도를 받아옴)
        String geoCodingUrl = UriComponentsBuilder.fromHttpUrl("http://api.openweathermap.org/geo/1.0/direct")
                .queryParam("q", city)
                .queryParam("limit", 1)
                .queryParam("appid", apiKey)
                .toUriString();

        GeocodeRes[] geocodeResponses = restTemplate.getForObject(geoCodingUrl, GeocodeRes[].class);
        if (geocodeResponses == null || geocodeResponses.length == 0) {
            return "City not found";
        }

        double lat = geocodeResponses[0].getLat();
        double lon = geocodeResponses[0].getLon();

        // 2. 5 Day / 3 Hour Forecast API
        String forecastUrl = UriComponentsBuilder.fromHttpUrl("https://api.openweathermap.org/data/2.5/forecast")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();

        return restTemplate.getForObject(forecastUrl, String.class);
    }
}
