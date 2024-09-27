package com.grepp.nbe1_2_team09.common.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TranslationUtil {

    private static final String API_KEY = "AIzaSyAquE_9PmI6XbFadiX3Gh8IjSK586ZYCPc";
    private static final String TRANSLATION_URL = "https://translation.googleapis.com/language/translate/v2";

    // 텍스트 번역 메서드
    public static String translateText(String text, String targetLanguage) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String urlStr = TRANSLATION_URL + "?q=" + text +
                "&target=" + targetLanguage + "&key=" + API_KEY;

        ResponseEntity<Map> response = restTemplate.getForEntity(urlStr, Map.class);
        Map<String, Object> jsonResponse = response.getBody();
        List<Map<String, Object>> translations = (List<Map<String, Object>>) ((Map<String, Object>) jsonResponse.get("data")).get("translations");
        return (String) translations.get(0).get("translatedText");
    }
}