package com.grepp.nbe1_2_team09.admin.dto;

import com.grepp.nbe1_2_team09.domain.entity.Role;
import com.grepp.nbe1_2_team09.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserInfoDTO extends User {

    private Long userId;

    private String username;

    private String email;

    private String password;

    private Role role;

}
