package com.hufs.dongri.dto.operator;

import com.hufs.dongri.domain.enums.FeeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeeStatusUpdateRequest {

    @NotNull(message = "회비 상태(PAID, UNPAID)가 필요합니다.")
    private FeeStatus status;
}