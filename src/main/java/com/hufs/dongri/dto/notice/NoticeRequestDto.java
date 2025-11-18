package com.hufs.dongri.dto.notice;

import com.hufs.dongri.domain.enums.NoticeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeRequestDto {

    @NotBlank
    private String title;

    private String content;

    @NotNull
    private NoticeType type;

    private LocalDateTime eventDate;
}