package com.hufs.dongri.dto.operator;

import com.hufs.dongri.domain.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AttendanceCheckRequest {

    @NotNull(message = "출석시킬 회원의 membershipId가 필요합니다.")
    private Long membershipId;

    @NotNull(message = "출석 상태(PRESENT, LATE, ABSENT, EXCUSED)가 필요합니다.")
    private AttendanceStatus status;
}