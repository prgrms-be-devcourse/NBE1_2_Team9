package com.grepp.nbe1_2_team09.domain.repository.user;

import com.grepp.nbe1_2_team09.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 제공자ID로 사용자 조회
    User findByProviderId(String providerId);

    // 회원가입 시 이메일 중복확인
    boolean existsByEmail(String email);

    @Query("SELECT u.username FROM User u WHERE u.userId = :userId")
    Optional<String> findUsernameById(@Param("userId") Long userId);

    Optional<User> findByUsername(String username);
}
