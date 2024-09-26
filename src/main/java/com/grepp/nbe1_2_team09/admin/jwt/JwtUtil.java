package com.grepp.nbe1_2_team09.admin.jwt;

import com.grepp.nbe1_2_team09.admin.dto.CustomUserInfoDTO;
import com.grepp.nbe1_2_team09.common.exception.exceptions.JwtException;
import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private final Key key;
    private final long accessTokenExpTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration_time}") long accessTokenExpTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    // AccessToken 생성
    public String createAccessToken(CustomUserInfoDTO user) {
        return createToken(user, accessTokenExpTime);
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
