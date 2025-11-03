package com.hufs.dongri.dto.club;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.enums.ClubCategory;
import lombok.Getter;

@Getter
public class ClubDetailDto {

    private Long clubId;
    private String name;
    private ClubCategory category;
    private String shortDescription;
    private String introduction; // [상세] 상세 소개글
    private String activitySchedule; // [상세] 활동 일정
    private String fee; // [상세] 회비
    private boolean isRecruiting;
    private String logoImageUrl;

    // Club 엔티티를 ClubDetailDto로 변환하는 생성자
    public ClubDetailDto(Club club) {
        this.clubId = club.getId();
        this.name = club.getName();
        this.category = club.getCategory();
        this.shortDescription = club.getShortDescription();
        this.introduction = club.getIntroduction();
        this.activitySchedule = club.getActivitySchedule();
        this.fee = club.getFee();
        this.isRecruiting = club.isRecruiting();
        this.logoImageUrl = club.getLogoImageUrl();
    }
}