package com.hufs.dongri.dto.club;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.enums.ClubCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClubDetailDto {

    private Long clubId;
    private String name;
    private ClubCategory category;
    private String shortDescription;
    private String introduction;
    private String activitySchedule;
    private String fee;
    private boolean isRecruiting;
    private String logoImageUrl;

    public static ClubDetailDto from(Club club) {
        return new ClubDetailDto(
                club.getId(),
                club.getName(),
                club.getCategory(),
                club.getShortDescription(),
                club.getIntroduction(),
                club.getActivitySchedule(),
                club.getFee(),
                club.isRecruiting(),
                club.getLogoImageUrl()
        );
    }
}