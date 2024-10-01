package com.grepp.nbe1_2_team09.admin.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.nbe1_2_team09.admin.dto.CustomUserInfoDTO;
import com.grepp.nbe1_2_team09.admin.redis.entity.RefreshToken;
import com.grepp.nbe1_2_team09.common.exception.exceptions.JwtException;
import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();   // JSON 직렬화/역직렬화용

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access_expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_expiration_time}") long refreshTokenExpTime,
            RedisTemplate<String, String> redisTemplate
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
        this.redisTemplate = redisTemplate;
    }

    // AccessToken 생성 (쿠키로 관리)
    public String createAccessToken(CustomUserInfoDTO user) {
        return createToken(user, accessTokenExpTime);
    }

    // RefreshToken 생성 후 Redis에 저장
    public String createRefreshToken(CustomUserInfoDTO user) {
        String token = createToken(user, refreshTokenExpTime);
        RefreshToken refreshToken = new RefreshToken(user.getEmail(), token);
        try {
            String refreshTokenJson = objectMapper.writeValueAsString(refreshToken);    // 객체를 JSON으로 변호나
            redisTemplate.opsForValue().set(user.getEmail(), refreshTokenJson, refreshTokenExpTime, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    }

    // JWT 생성
    private String createToken(CustomUserInfoDTO user, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Redis에서 RefreshToken 가져오기
    public RefreshToken getRefreshToken(String email) {
        String refreshTokenJson = redisTemplate.opsForValue().get(email);
        try {
            return objectMapper.readValue(refreshTokenJson, RefreshToken.class);    // JSON을 객체로 변환
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Redis에서 RefreshToken 삭제
    public void deleteRefreshToken(String email) {
        redisTemplate.delete(email);
    }

    // Token에서 User ID 추출
    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    // JWT 검증
    public boolean validateToken(String token) throws JwtException {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtException(ExceptionMessage.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new JwtException(ExceptionMessage.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new JwtException(ExceptionMessage.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new JwtException(ExceptionMessage.EMPTY_CLAIMS);
        }
    }

    // JWT Claims 추출
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
