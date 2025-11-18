package com.hufs.dongri.dto.operator;

import com.hufs.dongri.domain.Membership;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.ClubRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OperatorMemberResponse {

    private Long membershipId;
    private Long userId;
    private String name;
    private String studentId;
    private String major;
    private ClubRole clubRole;

    public static OperatorMemberResponse from(Membership membership) {
        User user = membership.getUser();
        return OperatorMemberResponse.builder()
                .membershipId(membership.getId())
                .userId(user.getId())
                .name(user.getName())
                .studentId(user.getStudentId())
                .major(user.getMajor())
                .clubRole(membership.getClubRole())
                .build();
    }
}