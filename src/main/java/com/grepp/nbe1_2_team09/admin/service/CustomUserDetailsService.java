package com.grepp.nbe1_2_team09.admin.service;

import com.grepp.nbe1_2_team09.admin.dto.CustomUserInfoDTO;
import com.grepp.nbe1_2_team09.common.exception.ExceptionMessage;
import com.grepp.nbe1_2_team09.common.exception.exceptions.UserException;
import com.grepp.nbe1_2_team09.domain.entity.user.User;
import com.grepp.nbe1_2_team09.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.USER_NOT_FOUND);
                    return new UserException(ExceptionMessage.USER_NOT_FOUND);
                });

        return createUserDetails(user);
    }

    public UserDetails createUserDetails(User user) {
        CustomUserInfoDTO customUserInfoDTO = new CustomUserInfoDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );

        return new CustomUserDetails(customUserInfoDTO);
    }
}
