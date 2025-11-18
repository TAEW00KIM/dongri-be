package com.hufs.dongri.repository;

import com.hufs.dongri.domain.Attendance;
import com.hufs.dongri.domain.Membership;
import com.hufs.dongri.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByMembershipAndNotice(Membership membership, Notice notice);
}