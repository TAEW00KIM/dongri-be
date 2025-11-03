// ApplicationDto.java (MASTER가 신청 목록 조회 시)
package com.hufs.dongri.dto.application;

import com.hufs.dongri.domain.AdminApplication;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import lombok.Getter;

@Getter
public class ApplicationDto {

    private Long applicationId;
    private ApplicationStatus status;
    private String reason;

    // 신청자 정보
    private Long applicantUserId;
    private String applicantName;
    private String applicantStudentId;

    // 동아리 정보
    private Long targetClubId;
    private String targetClubName;

    // AdminApplication 엔티티를 DTO로 변환하는 생성자
    public ApplicationDto(AdminApplication application) {
        this.applicationId = application.getId();
        this.status = application.getStatus();
        this.reason = application.getReason();

        this.applicantUserId = application.getApplicant().getId();
        this.applicantName = application.getApplicant().getName();
        this.applicantStudentId = application.getApplicant().getStudentId();

        this.targetClubId = application.getTargetClub().getId();
        this.targetClubName = application.getTargetClub().getName();
    }
}