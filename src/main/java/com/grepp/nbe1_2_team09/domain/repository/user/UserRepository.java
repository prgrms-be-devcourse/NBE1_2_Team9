package com.grepp.nbe1_2_team09.domain.repository.user;

import com.grepp.nbe1_2_team09.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
