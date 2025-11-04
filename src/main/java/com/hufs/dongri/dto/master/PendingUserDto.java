package com.hufs.dongri.dto.master;

import com.hufs.dongri.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingUserDto {
    private Long userId;
    private String email;
    private String name;
}