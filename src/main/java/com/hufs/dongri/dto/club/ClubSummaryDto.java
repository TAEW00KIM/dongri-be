package com.hufs.dongri.dto.club;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.enums.ClubCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClubSummaryDto {

    private Long clubId;
    private String name;
    private ClubCategory category;
    private String shortDescription;
    private String logoImageUrl;
    private boolean isRecruiting;

    public static ClubSummaryDto from(Club club) {
        return new ClubSummaryDto(
                club.getId(),
                club.getName(),
                club.getCategory(),
                club.getShortDescription(),
                club.getLogoImageUrl(),
                club.isRecruiting()
        );
    }
}