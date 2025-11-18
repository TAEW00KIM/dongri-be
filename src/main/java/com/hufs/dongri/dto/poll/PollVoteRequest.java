package com.hufs.dongri.dto.poll;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PollVoteRequest {

    @NotNull(message = "투표할 선택지 ID(optionId)가 필요합니다.")
    private Long optionId;
}