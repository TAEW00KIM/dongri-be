package com.hufs.dongri.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {

    // User 엔티티에서 받아올 필드들
    private String email;
    private String password;
    private String name;
    private String studentId;
    private String major;
}