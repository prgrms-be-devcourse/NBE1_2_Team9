package com.grepp.nbe1_2_team09.admin.service.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.nbe1_2_team09.admin.dto.CustomUserInfoDTO;
import com.grepp.nbe1_2_team09.admin.jwt.CookieUtil;
import com.grepp.nbe1_2_team09.admin.jwt.JwtUtil;
import com.grepp.nbe1_2_team09.domain.entity.user.OAuthProvider;
import com.grepp.nbe1_2_team09.domain.entity.user.Role;
import com.grepp.nbe1_2_team09.domain.entity.user.User;
import com.grepp.nbe1_2_team09.domain.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoApiService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    // 인가 코드로 액세스 토큰 요청
    public String getAccessToken(String code) throws JsonProcessingException {
        String tokenUri = "https://kauth.kakao.com/oauth/token";

        try {
            WebClient webClient = WebClient.create();

            String response = webClient.post()
                    .uri(tokenUri)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("client_id", clientId)
                            .with("redirect_uri", redirectUri)
                            .with("code", code)
                            .with("client_secret", clientSecret))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("AccessToken 요청 성공: {}", response);

            Map<String, Object> tokenData = new ObjectMapper().readValue(response, new TypeReference<Map<String, Object>>() {});
            return tokenData.get("access_token").toString();
        } catch (WebClientResponseException e) {
            log.error("AccessToken 요청 실패: {}", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("AccessToken 요청 중 에러 발생", e);
            throw new RuntimeException("카카오 액세스 토큰 요청 실패");
        }
    }

    // 액세스 토큰으로 사용자 정보 요청
    public KakaoUserInfo getUserInfo(String accessToken) throws JsonProcessingException {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        String response = WebClient.create(userInfoUri)
                .post()
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("사용자 정보 응답: {}", response);

        Map<String, Object> attributes = new ObjectMapper().readValue(response, new TypeReference<Map<String, Object>>() {});
        return new KakaoUserInfo(attributes);
    }

    // 사용자 정보 불러와서 DB 업데이트
    public User processUser(KakaoUserInfo kakaoUserInfo) {
        String providerId = kakaoUserInfo.getProviderId();
        User user = userRepository.findByProviderId(providerId);

        if (user == null) {
            // 새로운 유저인 경우 DB에 등록
            String defaultEmail = "user" + providerId + "@kakao.com";
            String defaultPassword = "kakao_default_password";

            user = User.builder()
                    .provider(OAuthProvider.KAKAO)
                    .providerId(providerId)
                    .username(kakaoUserInfo.getName())
                    .email(defaultEmail)
                    .password(defaultPassword)
                    .role(Role.MEMBER)
                    .build();
            userRepository.save(user);
            log.info("신규 유저 등록: {}", kakaoUserInfo.getName());
        } else {
            log.info("기존 유저 로그인: {}", kakaoUserInfo.getName());
        }

        return user;
    }

}
