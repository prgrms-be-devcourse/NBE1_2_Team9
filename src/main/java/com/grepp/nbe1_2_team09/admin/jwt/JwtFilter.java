package com.grepp.nbe1_2_team09.admin.jwt;

import com.grepp.nbe1_2_team09.admin.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
            // 사용자 ID 추출해서 UserDetails 로드
            Long userId = jwtUtil.getUserId(jwt);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId.toString());

            // UserDetails가 유효한 경우
            if (userDetails != null) {
                // 인증 객체 생성 후 SpringContext에 설정
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 세부 정보 설정 (IP, 세션 정보)
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
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
