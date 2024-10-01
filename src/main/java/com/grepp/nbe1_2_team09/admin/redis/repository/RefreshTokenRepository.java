package com.grepp.nbe1_2_team09.admin.redis.repository;

import com.grepp.nbe1_2_team09.admin.redis.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<String, String> {
}
