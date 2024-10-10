package com.grepp.nbe1_2_team09.controller.user;

import com.grepp.nbe1_2_team09.admin.jwt.CookieUtil;
import com.grepp.nbe1_2_team09.admin.service.oauth2.KakaoApiService;
import com.grepp.nbe1_2_team09.admin.service.oauth2.KakaoUserInfo;
import com.grepp.nbe1_2_team09.common.exception.exceptions.UserException;
import com.grepp.nbe1_2_team09.controller.user.dto.*;
import com.grepp.nbe1_2_team09.domain.entity.user.User;
import com.grepp.nbe1_2_team09.domain.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final KakaoApiService kakaoApiService;

    // 사용자 정보 요청 API
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String token = CookieUtil.getAccessTokenFromCookies(request);

        try {
            UserInfoResp currentUser = userService.getCurrentUserDTO(token);
            return ResponseEntity.ok(currentUser);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpReq signUpReq) {
        userService.register(signUpReq);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody SignInReq signInReq, HttpServletResponse response) {
        String token = userService.signIn(signInReq, response);
        return ResponseEntity.ok("로그인 성공. 토큰: " + token);
    }

    // 소셜 로그인
    @GetMapping("/signin/kakao")
    public ResponseEntity<String> kakaoLogin(@RequestParam String code, HttpServletResponse response) {
        log.info("카카오 인가 코드: {}", code);

        try {
            String accessToken = kakaoApiService.getAccessToken(code);

            KakaoUserInfo kakaoUserInfo = kakaoApiService.getUserInfo(accessToken);

            User user = kakaoApiService.processUser(kakaoUserInfo);

            kakaoApiService.createJwtToken(user, response);

            response.sendRedirect("http://localhost:3000/");

            return ResponseEntity.ok("카카오 로그인 성공, JWT 토큰이 쿠키에 저장되었습니다.");
        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카카오 로그인 실패");
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자가 로그인되지 않았습니다.");
        }

        String email = principal.getName();
        userService.logout(email, response);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 회원 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    // 회원 정보 수정
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileReq updateProfileReq, Principal principal ) {
        Long loggedInUserId = Long.parseLong(principal.getName());
        userService.updateProfile(loggedInUserId, userId, updateProfileReq);
        return ResponseEntity.ok("회원 정보 수정 성공");
    }

    // 비밀번호 변경
    @PutMapping("/{userId}/password")
    public ResponseEntity<String> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordReq changePasswordReq, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자가 로그인되지 않았습니다.");
        }

        Long loggedInUserId = Long.parseLong(principal.getName());
        userService.changePassword(loggedInUserId, userId, changePasswordReq);
        return ResponseEntity.ok("비밀번호 변경 성공");
    }

    // 회원 정보 삭제 (회원 탈퇴)
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId, Principal principal) {
        Long loggedInUserId = Long.parseLong(principal.getName());
        userService.deleteUser(loggedInUserId, userId);
        return ResponseEntity.ok("회원 탈퇴 성공");
    }

}
