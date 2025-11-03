// ApplicationRequestDto.java (USER가 신청 시)
package com.hufs.dongri.dto.application;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplicationRequestDto {

    private Long clubId; // 신청할 동아리 ID
    private String reason; // 신청 사유
}