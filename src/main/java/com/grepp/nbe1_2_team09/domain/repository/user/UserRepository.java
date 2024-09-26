package com.grepp.nbe1_2_team09.domain.repository.user;

import com.grepp.nbe1_2_team09.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 회원가입 시 이메일 중복확인
    boolean existByEmail(String email);
}
