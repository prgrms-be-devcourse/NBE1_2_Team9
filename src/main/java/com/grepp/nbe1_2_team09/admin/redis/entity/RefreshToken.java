package com.grepp.nbe1_2_team09.admin.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken implements Serializable {

    private String id;
    private String refreshToken;
}
