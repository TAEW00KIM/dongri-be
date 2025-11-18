package com.hufs.dongri.dto.user;

import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.GlobalRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserMyInfoResponse {
    private Long userId;
    private String email;
    private String name;
    private String studentId;
    private String major;
    private GlobalRole globalRole;

    public static UserMyInfoResponse from(User user) {
        return UserMyInfoResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .studentId(user.getStudentId())
                .major(user.getMajor())
                .globalRole(user.getGlobalRole())
                .build();
    }
}