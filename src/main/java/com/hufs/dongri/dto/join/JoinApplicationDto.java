package com.hufs.dongri.dto.join;

import com.hufs.dongri.domain.JoinApplication;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import lombok.Getter;

@Getter
public class JoinApplicationDto {

    private Long applicationId;
    private ApplicationStatus status;
    private String reason;

    private Long applicantUserId;
    private String applicantName;
    private String applicantStudentId;
    private String applicantMajor;

    public JoinApplicationDto(JoinApplication app) {
        this.applicationId = app.getId();
        this.status = app.getStatus();
        this.reason = app.getReason();
        this.applicantUserId = app.getUser().getId();
        this.applicantName = app.getUser().getName();
        this.applicantStudentId = app.getUser().getStudentId();
        this.applicantMajor = app.getUser().getMajor();
    }
}