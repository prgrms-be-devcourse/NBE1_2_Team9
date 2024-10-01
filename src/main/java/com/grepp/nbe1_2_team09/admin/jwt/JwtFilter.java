package com.grepp.nbe1_2_team09.admin.jwt;

import com.grepp.nbe1_2_team09.admin.dto.CustomUserInfoDTO;
import com.grepp.nbe1_2_team09.admin.redis.entity.RefreshToken;
import com.grepp.nbe1_2_team09.admin.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {   // OncePerRequestFilter -> 한 번 실행 보장

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    private static final String ACCESS_HEADER = "Authorization";    // 토큰이 담긴 헤더 이름
    private static final String BEARER_TYPE = "Bearer";             // 토큰 타입 (Bearer)

    // JWT 토큰 검증 후, 유효하면 SecurityContext에 사용자 인증 정보를 설정
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);

        // 토큰이 유효하고, JWT 유효성 검증 통과 시
        if (StringUtils.hasText(jwt)) {
            try {
                // 토큰 검증
                if (jwtUtil.validateToken(jwt)) {
                    // 유효한 AccessToken일 경우 사용자 인증 처리
                    Long userId = jwtUtil.getUserId(jwt);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId.toString());

                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                // AccessToken이 만료된 경우 RefreshToken으로 재발급
                String refreshToken = CookieUtil.getRefreshTokenFromCookies(request);
                if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                    Long userId = jwtUtil.getUserId(refreshToken);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId.toString());
                    if (userDetails != null) {
                        // 새로 발급한 AccessToken을 쿠키에 저장
                        String newAccessToken = jwtUtil.createAccessToken((CustomUserInfoDTO) userDetails);
                        CookieUtil.createAccessTokenCookie(newAccessToken, response);

                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    // HTTP 요청에서 JWT 토큰을 추출
    private String resolveToken(HttpServletRequest request) {
        // 요청 헤더에서 Authorization 헤더의 값을 가져옴
        String bearerToken = request.getHeader(ACCESS_HEADER);

        // Authorization 헤더에 Bearer 타입의 토큰이 있는지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            // Bearer 이후의 실제 토큰만 반환
            return bearerToken.substring(7);
        }

        return null;
    }
}
