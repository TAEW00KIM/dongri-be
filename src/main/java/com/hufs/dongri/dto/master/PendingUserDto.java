package com.hufs.dongri.dto.master;

import com.hufs.dongri.domain.User;
import lombok.Getter;

@Getter
public class PendingUserDto {
    private Long userId;
    private String email;
    private String name;

    public PendingUserDto(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
    }
}