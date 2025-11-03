// RejectDto.java (MASTER가 거절 시)
package com.hufs.dongri.dto.application;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RejectDto {

    private String reason; // 거절 사유
}