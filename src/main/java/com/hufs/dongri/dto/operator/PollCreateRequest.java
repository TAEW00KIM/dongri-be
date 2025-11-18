package com.hufs.dongri.dto.operator;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PollCreateRequest {

    @NotBlank(message = "투표 제목을 입력해주세요.")
    private String title;

    @NotNull(message = "선택지 목록이 필요합니다.")
    @Size(min = 2, message = "선택지는 2개 이상이어야 합니다.")
    private List<String> optionTexts;

    @Future(message = "마감 기한은 현재 시간 이후여야 합니다.")
    private LocalDateTime deadline;

    @NotNull
    private Boolean isAnonymous;
}