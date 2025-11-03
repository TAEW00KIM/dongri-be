// AdminApplicationRepository.java
package com.hufs.dongri.repository;

import com.hufs.dongri.domain.AdminApplication;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminApplicationRepository extends JpaRepository<AdminApplication, Long> {

    // 1. (MASTER) 승인 대기중인 신청서 목록 조회
    List<AdminApplication> findByStatus(ApplicationStatus status);

    // 2. (USER) 이미 '대기중'인 신청서가 있는지 확인 (중복 신청 방지)
    boolean existsByApplicantAndStatus(User applicant, ApplicationStatus status);
}