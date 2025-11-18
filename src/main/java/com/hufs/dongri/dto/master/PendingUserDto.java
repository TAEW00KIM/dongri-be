package com.hufs.dongri.dto.master;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingUserDto {
    private Long userId;
    private String email;
    private String name;
}