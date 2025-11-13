package com.hufs.dongri.repository;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.JoinApplication;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinApplicationRepository extends JpaRepository<JoinApplication, Long> {
    boolean existsByUserAndClubAndStatus(User user, Club club, ApplicationStatus status);

    // List<JoinApplication> findByClubAndStatus(Club club, ApplicationStatus status);
}