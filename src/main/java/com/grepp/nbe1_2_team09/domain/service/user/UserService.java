package com.grepp.nbe1_2_team09.domain.service.user;

import com.grepp.nbe1_2_team09.admin.dto.CustomUserInfoDTO;
import com.grepp.nbe1_2_team09.admin.jwt.JwtUtil;
import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.UserException;
import com.grepp.nbe1_2_team09.controller.user.dto.SignInReq;
import com.grepp.nbe1_2_team09.controller.user.dto.SignUpReq;
import com.grepp.nbe1_2_team09.controller.user.dto.UpdateProfileReq;
import com.grepp.nbe1_2_team09.domain.entity.Role;
import com.grepp.nbe1_2_team09.domain.entity.User;
import com.grepp.nbe1_2_team09.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService  {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Transactional
    public User register(SignUpReq signUpReq) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(signUpReq.getEmail()).isPresent()) {
            throw new UserException(ExceptionMessage.USER_IS_PRESENT);
        }

        // 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(signUpReq.getPassword());

        User user = User.builder()
                .username(signUpReq.getUsername())
                .email(signUpReq.getEmail())
                .password(encodePassword)
                .role(Role.MEMBER)      // default Role -> MEMBER
                .build();

        return userRepository.save(user);
    }

    // 로그인
    public String signIn(SignInReq signInReq) {
        User user = findByEmailOrThrowUserException(signInReq.getEmail());

        if (!passwordEncoder.matches(signInReq.getPassword(), user.getPassword())) {
            throw new UserException(ExceptionMessage.USER_LOGIN_FAIL);
        }

        user.updateLastLoginDate();
        userRepository.save(user);

        CustomUserInfoDTO customUserInfoDTO = new CustomUserInfoDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );

        return jwtUtil.createAccessToken(customUserInfoDTO);
    }

    // 회원 정보 조회
    public User getUser(String email) {
        return findByEmailOrThrowUserException(email);
    }

    // 회원 정보 수정
    @Transactional
    public void updateProfile(Long userId, UpdateProfileReq updateProfileReq) {
        User user = findByIdOrThrowUserException(userId);
        user.updateProfile(updateProfileReq.getUsername(), updateProfileReq.getEmail());
    }

    // 회원 정보 삭제
    @Transactional
    public void deleteUser(Long userId) {
        User user = findByIdOrThrowUserException(userId);
        userRepository.delete(user);
        log.info("사용자 정보가 삭제되었습니다. userId: {}", userId);
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, String newPassword) {
        User user = findByIdOrThrowUserException(userId);
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedPassword);
    }

    // ID로 찾기
    public User findByIdOrThrowUserException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.USER_NOT_FOUND);
                    return new UserException(ExceptionMessage.USER_NOT_FOUND);
                });
    }

    // Email로 찾기
    public User findByEmailOrThrowUserException(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", email, ExceptionMessage.USER_NOT_FOUND);
                    return new UserException(ExceptionMessage.USER_NOT_FOUND);
                });
    }

    public void logout(String token) {
    }
}