package com.grepp.nbe1_2_team09.controller.user;

import com.grepp.nbe1_2_team09.controller.user.dto.SignInReq;
import com.grepp.nbe1_2_team09.controller.user.dto.SignUpReq;
import com.grepp.nbe1_2_team09.controller.user.dto.UpdateProfileReq;
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

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자가 로그인되어 있지 않습니다.");
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
    public ResponseEntity<String> updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileReq updateProfileReq) {
        userService.updateProfile(userId, updateProfileReq);
        return ResponseEntity.ok("회원 정보 수정 성공");
    }

    // 비밀번호 변경
    @PutMapping("/{userId}/password")
    public ResponseEntity<String> changePassword(@PathVariable Long userId, @RequestParam String newPassword) {
        userService.changePassword(userId, newPassword);
        return ResponseEntity.ok("비밀번호 변경 성공");
    }

    // 회원 정보 삭제 (회원 탈퇴)
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("회원 탈퇴 성공");
    }
}
