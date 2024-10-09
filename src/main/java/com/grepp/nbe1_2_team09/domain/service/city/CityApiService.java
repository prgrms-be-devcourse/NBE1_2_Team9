package com.grepp.nbe1_2_team09.domain.service.city;

import com.grepp.nbe1_2_team09.controller.city.dto.CityResponse;
import com.grepp.nbe1_2_team09.controller.location.dto.PlaceDetailResponse;
import com.grepp.nbe1_2_team09.controller.location.dto.api.GooglePlacesAutocompleteResponse;
import com.grepp.nbe1_2_team09.controller.location.dto.api.GooglePlacesNearbyResponse;
import com.grepp.nbe1_2_team09.controller.location.dto.api.PlaceDetailApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityApiService {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();


    // 도시 검색
    public List<CityResponse> searchCity(String query) {
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + query + "&types=(cities)&key=" + apiKey;
        ResponseEntity<GooglePlacesAutocompleteResponse> response = restTemplate.getForEntity(url, GooglePlacesAutocompleteResponse.class);

        return response.getBody().predictions()
                .stream()
                .map(prediction -> new CityResponse(prediction.place_id(), prediction.description()))
                .collect(Collectors.toList());
    }

    //나라별 도시 검색
    public List<CityResponse> getCitiesByCountry(String country) {
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=cities+in+"
                + country + "&key=" + apiKey;
        ResponseEntity<GooglePlacesNearbyResponse> response = restTemplate.getForEntity(url, GooglePlacesNearbyResponse.class);

        return response.getBody().results()
                .stream()
                .map(prediction -> new CityResponse(prediction.place_id(), prediction.name()))
                .collect(Collectors.toList());
    }


}

