package com.grepp.nbe1_2_team09.domain.repository;

import com.grepp.nbe1_2_team09.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
