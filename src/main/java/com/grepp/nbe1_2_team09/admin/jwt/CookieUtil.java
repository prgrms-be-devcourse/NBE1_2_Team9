package com.grepp.nbe1_2_team09.admin.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "AccessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshToken";
    private static final int ACCESS_TOKEN_EXPIRY = 60 * 60;                 // 1시간
    private static final int REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60;       // 7일

    // AccessToken 쿠키 생성
    public static void createAccessTokenCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);     // HTTPS에서만 전송되도록 설정
        cookie.setPath("/");        // 모든 경로에서 사용 가능
        cookie.setMaxAge(ACCESS_TOKEN_EXPIRY);
        response.addCookie(cookie);
    }

    // RefreshToken 쿠키 생성
    public static void createRefreshTokenCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(REFRESH_TOKEN_EXPIRY);
        response.addCookie(cookie);
    }

    // 쿠키에서 AccessToken 가져오기
    public static String getAccessTokenFromCookies(HttpServletRequest request) {
        return getTokenFromCookies(request, ACCESS_TOKEN_COOKIE_NAME);
    }

    // 쿠키에서 RefreshToken 가져오기
    public static String getRefreshTokenFromCookies(HttpServletRequest request) {
        return getTokenFromCookies(request, REFRESH_TOKEN_COOKIE_NAME);
    }

    // 쿠키에서 토큰을 가져오는 공통 메서드
    private static String getTokenFromCookies(HttpServletRequest request, String tokenName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // AccessToken 쿠키 삭제
    public static void deleteAccessTokenCookie(HttpServletResponse response) {
        deleteCookie(response, ACCESS_TOKEN_COOKIE_NAME);
    }

    // RefreshToken 쿠키 삭제
    public static void deleteRefreshTokenCookie(HttpServletResponse response) {
        deleteCookie(response, REFRESH_TOKEN_COOKIE_NAME);
    }

    // 쿠키 삭제 공통 메서드
    private static void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 삭제
        response.addCookie(cookie);
    }

}
