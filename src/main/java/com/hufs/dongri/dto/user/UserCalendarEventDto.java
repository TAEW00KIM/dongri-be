package com.hufs.dongri.dto.user;

import com.hufs.dongri.domain.Notice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserCalendarEventDto {

    private Long noticeId;
    private String title;
    private LocalDateTime eventDate;
    private Long clubId;
    private String clubName;

    public static UserCalendarEventDto from(Notice notice) {
        return UserCalendarEventDto.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .eventDate(notice.getEventDate())
                .clubId(notice.getClub().getId())
                .clubName(notice.getClub().getName())
                .build();
    }
}