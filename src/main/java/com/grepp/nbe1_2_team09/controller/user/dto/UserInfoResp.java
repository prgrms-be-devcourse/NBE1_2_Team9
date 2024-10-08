package com.grepp.nbe1_2_team09.controller.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResp {
    private Long userId;
    private String username;
    private String email;
    private String role;
    private LocalDateTime joinedDate;
}
