package com.grepp.nbe1_2_team09.controller.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpReq {
    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String password;
}
