package com.hufs.dongri.dto.club;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.enums.ClubCategory;
import lombok.Getter;

@Getter
public class ClubSummaryDto {

    private Long clubId;
    private String name;
    private ClubCategory category;
    private String shortDescription; // 짧은 설명
    private String logoImageUrl;
    private boolean isRecruiting;

    // Club 엔티티를 ClubSummaryDto로 변환하는 생성자
    public ClubSummaryDto(Club club) {
        this.clubId = club.getId();
        this.name = club.getName();
        this.category = club.getCategory();
        this.shortDescription = club.getShortDescription();
        this.logoImageUrl = club.getLogoImageUrl();
        this.isRecruiting = club.isRecruiting();
    }
}