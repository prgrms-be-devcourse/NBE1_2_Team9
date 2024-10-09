package com.grepp.nbe1_2_team09.domain.service.location;

import com.grepp.nbe1_2_team09.controller.event.dto.EventDto;
import com.grepp.nbe1_2_team09.controller.location.dto.*;
import com.grepp.nbe1_2_team09.controller.location.dto.api.*;
import com.grepp.nbe1_2_team09.domain.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationApiService {

    private final RestTemplate restTemplate;
    private final EventService eventService;

    @Value("${google.api.key}")
    private String apiKey;

    //장소 자동 검색
    public List<PlaceResponse> getAutocompletePlaces(Long eventId, String input) {
       // 이벤트 ID로 이벤트 정보 가져오기
        EventDto eventDto = eventService.getEventById(eventId);

        String cityName = eventDto.city();

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
    public List<PlaceRecommendResponse> getRecommendedPlaces(Long eventId, String type) {

        // 이벤트 ID로 이벤트 정보 가져오기
        EventDto eventDto = eventService.getEventById(eventId);

        String cityName = eventDto.city();

        // 도시 이름으로 위도/경도 가져오기
        String location = getCoordinatesFromCityName(cityName);

        String placeType = (type != null) ? type : "establishment";

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location
                + "&radius=5000&type=" + placeType + "&key=" + apiKey;

        ResponseEntity<GooglePlacesNearbyResponse> response = restTemplate.getForEntity(url, GooglePlacesNearbyResponse.class);

        return response.getBody().results()
                .stream()
                .map(result -> new PlaceRecommendResponse(result.place_id(), result.name(),result.geometry().location().lat(),result.geometry().location().lng(),Optional.ofNullable(result.rating()).orElse(0.0), result.user_ratings_total(),result.vicinity() ,getPhotoUrl(result.photos())))
                .collect(Collectors.toList());
    }

    // id로 장소 상세 정보 조회
    public PlaceDetailResponse getPlaceDetail(String placeId) {
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + apiKey;

        // Google Places API로 요청을 보내고 응답을 PlaceDetailApiResponse로 매핑
        ResponseEntity<PlaceDetailApiResponse> response = restTemplate.getForEntity(url, PlaceDetailApiResponse.class);

        // 응답의 result 객체를 사용하여 PlaceDetailResponse를 반환
        PlaceDetailApiResponse.Result result = response.getBody().result();

        // 첫 번째 사진의 URL을 가져옵니다.
        String photoUrl = getPhotoUrl2(result.photos());

        // 현재 영업 여부와 요일별 영업 시간 정보
        boolean openNow = result.current_opening_hours() != null && result.current_opening_hours().open_now();
        String weekdayText = result.current_opening_hours() != null
                ? String.join(", ", result.current_opening_hours().weekday_text())
                : "운영 시간 정보가 없습니다.";

        return new PlaceDetailResponse(
                result.place_id(),
                result.name(),
                result.geometry().location().lat(),
                result.geometry().location().lng(),
                result.formatted_address(),
                result.formatted_phone_number(),
                photoUrl != null ? new String(photoUrl) : null,
                result.rating(),
                result.url(),
                weekdayText,  // 요일별 영업 시간 정보 추가
                result.website(),
                openNow  // 현재 영업 여부 추가
        );
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private String getPhotoUrl(List<GooglePlacesNearbyResponse.Photo> photos) {
        if (photos != null && !photos.isEmpty()) {
            return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + photos.get(0).photo_reference() + "&key=" + apiKey;
        }
        return null;
    }

    private String getPhotoUrl2(List<PlaceDetailApiResponse.Photo> photos) {
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

