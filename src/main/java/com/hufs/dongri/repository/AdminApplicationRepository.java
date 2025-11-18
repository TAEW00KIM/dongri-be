package com.hufs.dongri.repository;

import com.hufs.dongri.domain.AdminApplication;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminApplicationRepository extends JpaRepository<AdminApplication, Long> {

    List<AdminApplication> findByStatus(ApplicationStatus status);

    boolean existsByApplicantAndStatus(User applicant, ApplicationStatus status);
}