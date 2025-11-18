package com.hufs.dongri.dto.user;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.Membership;
import com.hufs.dongri.domain.enums.ClubCategory;
import com.hufs.dongri.domain.enums.ClubRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserMyClubResponse {

    private Long clubId;
    private String name;
    private ClubCategory category;
    private String shortDescription;
    private String logoImageUrl;
    private ClubRole myClubRole;

    public static UserMyClubResponse from(Membership membership) {
        Club club = membership.getClub();
        return UserMyClubResponse.builder()
                .clubId(club.getId())
                .name(club.getName())
                .category(club.getCategory())
                .shortDescription(club.getShortDescription())
                .logoImageUrl(club.getLogoImageUrl())
                .myClubRole(membership.getClubRole())
                .build();
    }
}