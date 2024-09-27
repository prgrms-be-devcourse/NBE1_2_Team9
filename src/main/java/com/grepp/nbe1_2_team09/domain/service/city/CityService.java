package com.grepp.nbe1_2_team09.domain.service.city;

import com.grepp.nbe1_2_team09.controller.city.dto.CityResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CityService {

    private static final String API_KEY = "AIzaSyAquE_9PmI6XbFadiX3Gh8IjSK586ZYCPc";

    // 도시 정보 URL 생성
    public String getCityInfoUrl(String city) {
        return "https://maps.googleapis.com/maps/api/place/textsearch/json?query="
                + URLEncoder.encode(city, StandardCharsets.UTF_8) + "&key=" + API_KEY;
    }

    // 검색한 도시 나라 검색 URL 생성
    public String getCitiesInCountryUrl(String country) {
        String query = "cities in " + country; // 'cities in 일본' 형식
        return "https://maps.googleapis.com/maps/api/place/textsearch/json?query="
                + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&key=" + API_KEY;
    }

    // 나라의 도시 검색
    public List<CityResponse> searchCities(String city) throws Exception {
        // 한글 도시 이름을 영어로 번역
        String translatedCity = translateText(city, "en");

        //입력된 도시 정보를 검색
        String cityInfoUrl = getCityInfoUrl(translatedCity);
        HttpURLConnection conn = (HttpURLConnection) new URL(cityInfoUrl).openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //JSON 응답 파싱
        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray results = jsonResponse.getJSONArray("results");

        if (results.length() > 0) {
            JSONObject cityData = results.getJSONObject(0);
            String country = cityData.getString("formatted_address"); // 전체 주소에서 나라를 추출
            // 국가 이름 추출 (예: "Tokyo, Japan" -> "Japan")
            String[] addressParts = country.split(", ");
            String countryName = addressParts[addressParts.length - 1];

            //해당 나라의 도시 검색
            String citiesInCountryUrl = getCitiesInCountryUrl(countryName);
            HttpURLConnection citiesConn = (HttpURLConnection) new URL(citiesInCountryUrl).openConnection();
            citiesConn.setRequestMethod("GET");

            BufferedReader citiesIn = new BufferedReader(new InputStreamReader(citiesConn.getInputStream()));
            StringBuilder citiesResponse = new StringBuilder();
            String citiesInputLine;

            while ((citiesInputLine = citiesIn.readLine()) != null) {
                citiesResponse.append(citiesInputLine);
            }
            citiesIn.close();

        //도시 리스트 생성
        Set<String> cityNamesSet = new HashSet<>(); // 중복을 제거하기 위한 Set
        List<CityResponse> citiesList = new ArrayList<>();
        JSONObject citiesJsonResponse = new JSONObject(citiesResponse.toString());
        JSONArray citiesResults = citiesJsonResponse.getJSONArray("results");

        for (int i = 0; i < Math.min(6, citiesResults.length()); i++) { // 6개 도시만 보여주기
            JSONObject cityItem = citiesResults.getJSONObject(i);
            String cityName = cityItem.getString("name");

            // 국가명이 포함된 경우 저장하지 않음
            if (cityName.contains(countryName)) {
                continue; // 도시 이름에 국가명이 포함된 경우 스킵
            }

            // 중복 여부 확인
            if (!cityNamesSet.contains(cityName)) {
                // 중복되지 않은 도시 이름을 Set에 추가
                cityNamesSet.add(cityName);
                // 영어 도시 이름을 한국어로 번역
                String translatedCityName = translateText(cityName, "ko");

                double latitude = cityItem.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double longitude = cityItem.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                citiesList.add(new CityResponse(translatedCityName, latitude, longitude)); // 한글로 변환된 도시 이름 추가
            }
        }
        return citiesList; // 도시 리스트 반환
    } else {
        return new ArrayList<>(); // 빈 리스트 반환
    }
    }

    // 텍스트 번역 메소드
    private String translateText(String text, String targetLanguage) throws Exception {
        String apiKey = API_KEY;
        String urlStr = "https://translation.googleapis.com/language/translate/v2?q=" +
                URLEncoder.encode(text, StandardCharsets.UTF_8) +
                "&target=" + targetLanguage +
                "&key=" + apiKey;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // JSON 응답 파싱 및 번역된 텍스트 반환
        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("translatedText");
    }
}
