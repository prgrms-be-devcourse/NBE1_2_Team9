package com.grepp.nbe1_2_team09.domain.service.city;

import com.grepp.nbe1_2_team09.controller.city.dto.SearchCityResponse;
import com.grepp.nbe1_2_team09.controller.location.dto.api.GooglePlacesAutocompleteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityApiService {

    private final String API_KEY = "AIzaSyAquE_9PmI6XbFadiX3Gh8IjSK586ZYCPc";  // Google Places API 키
    private final RestTemplate restTemplate = new RestTemplate();


    // 도시 검색
    public List<SearchCityResponse> searchCity(String query) {
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + query + "&types=(cities)&key=" + API_KEY;
        ResponseEntity<GooglePlacesAutocompleteResponse> response = restTemplate.getForEntity(url, GooglePlacesAutocompleteResponse.class);

        return response.getBody().predictions()
                .stream()
                .map(prediction -> new SearchCityResponse(prediction.place_id(), prediction.description()))
                .collect(Collectors.toList());
    }



   /* // 나라 기반으로 도시 목록 검색
    public List<SearchCityResp> searchCitiesByCountry(String countryCode) {
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=city&types=(cities)&components=country:" + countryCode + "&key=" + API_KEY;
        ResponseEntity<GooglePlacesAutocompleteResponse> response = restTemplate.getForEntity(url, GooglePlacesAutocompleteResponse.class);

        return response.getBody().predictions()
                .stream()
                .map(prediction -> new SearchCityResp(prediction.place_id(), prediction.description(), null))
                .collect(Collectors.toList());
    }*/
}

