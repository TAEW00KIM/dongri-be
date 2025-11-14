// MembershipRepository.java
package com.hufs.dongri.repository;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.Membership;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.ClubRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    // 1. (USER) 이미 동아리에 가입(MEMBER 또는 ADMIN)되어 있는지 확인 (중복 신청 방지)
    boolean existsByUserAndClub(User user, Club club);

    boolean existsByUserAndClubAndClubRole(User user, Club club, ClubRole clubRole);

    Optional<Membership> findByUserAndClub(User user, Club club);
}