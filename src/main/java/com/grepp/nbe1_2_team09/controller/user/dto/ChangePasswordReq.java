package com.grepp.nbe1_2_team09.controller.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordReq {

    private String currentPassword;

    private String newPassword;

}
