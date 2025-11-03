package com.hufs.dongri.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDto {

    // "Bearer " 접두사를 포함한 JWT
    private String accessToken;
}