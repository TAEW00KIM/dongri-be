package com.hufs.dongri.dto.application;

import com.hufs.dongri.domain.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationDto {

    private Long applicationId;
    private ApplicationStatus status;
    private String reason;
    private Long applicantUserId;
    private String applicantName;
    private String applicantStudentId;
    private Long targetClubId;
    private String targetClubName;
}