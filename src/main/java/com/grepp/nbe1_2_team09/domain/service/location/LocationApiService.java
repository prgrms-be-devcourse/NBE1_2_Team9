package com.grepp.nbe1_2_team09.domain.service.location;

import com.grepp.nbe1_2_team09.controller.location.dto.*;
import com.grepp.nbe1_2_team09.controller.location.dto.api.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationApiService {

    private final RestTemplate restTemplate;
    private static final String apiKey = "AIzaSyAquE_9PmI6XbFadiX3Gh8IjSK586ZYCPc";

    //장소 자동 검색
    public List<PlaceResponse> getAutocompletePlaces(Long eventId, String input) {
        /*// 이벤트 ID로 이벤트 정보가져오기
        EventDto eventDto = eventService.getEventById(eventId);

          // 도시 name로 위도와 경도 가져오기
        String cityname = eventDto.getCityname();*/

        //임의로
        String cityName = "seoul";
        // 도시 이름으로 위도/경도 가져오기
        String location = getCoordinatesFromCityName(cityName);

        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + input +
                "&location=" + location + "&radius=5000&types=establishment&key=" + apiKey;

        ResponseEntity<GooglePlacesAutocompleteResponse> response = restTemplate.getForEntity(url, GooglePlacesAutocompleteResponse.class);

        return response.getBody().predictions()
                .stream()
                .map(prediction -> new PlaceResponse(prediction.place_id(), prediction.description(), null))
                .collect(Collectors.toList());
    }

    //장소 추천
    public List<PlaceResponse> getRecommendedPlaces(Long eventId, String type) {

        /*// eventId를 사용하여 이벤트 정보 가져오기
        EventDto eventDto = eventService.getEventById(eventId);

        Long cityId = eventDto.getCityId();*/

        //임의로
        String cityName = "seoul";
        // 도시 이름으로 위도/경도 가져오기
        String location = getCoordinatesFromCityName(cityName);

        String placeType = (type != null) ? type : "establishment";
        System.out.println("tytttye"+placeType);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location
                + "&radius=5000&type=" + placeType + "&key=" + apiKey;

        ResponseEntity<GooglePlacesNearbyResponse> response = restTemplate.getForEntity(url, GooglePlacesNearbyResponse.class);

        return response.getBody().results()
                .stream()
                .map(result -> new PlaceResponse(result.place_id(), result.name(), getPhotoUrl(result.photos())))
                .collect(Collectors.toList());
    }

    //id로 장소 상세 정보 조회
    public PlaceDetailResponse getPlaceDetail(String placeId) {
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + apiKey;

        // Google Places API로 요청을 보내고 응답을 PlaceDetailApiResponse로 매핑
        ResponseEntity<PlaceDetailApiResponse> response = restTemplate.getForEntity(url, PlaceDetailApiResponse.class);

        // 응답의 result 객체를 사용하여 PlaceDetailResponse를 반환
        PlaceDetailApiResponse.Result result = response.getBody().result();

        return new PlaceDetailResponse(
                result.place_id(),
                result.name(),
                result.formatted_address(),
                result.formatted_phone_number(),
                result.photos() != null ? new PlaceDetailResponse.Photo(result.photos().photo_reference()) : null, // 사진을 단일로 반환
                result.rating(),
                result.url()
        );

    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private String getPhotoUrl(List<GooglePlacesNearbyResponse.Result.Photo> photos) {
        if (photos != null && !photos.isEmpty()) {
            return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + photos.get(0).photo_reference() + "&key=" + apiKey;
        }
        return null;
    }

    public String getCoordinatesFromCityName(String cityName) {
        String geocodingUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + cityName + "&key=" + apiKey;

        // Geocoding API 호출해서 위도/경도 가져오기
        ResponseEntity<GeocodingApiResponse> geocodingResponse = restTemplate.getForEntity(geocodingUrl, GeocodingApiResponse.class);

        // 첫 번째 결과에서 위도와 경도 가져오기
        GeocodingApiResponse.Result geocodingResult = geocodingResponse.getBody().results().get(0);
        double latitude = geocodingResult.geometry().location().lat();
        double longitude = geocodingResult.geometry().location().lng();

        // 위도와 경도를 "위도,경도" 형식의 문자열로 반환
        return latitude + "," + longitude;
    }



}

