package com.hufs.dongri.repository;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.Membership;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.ClubRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    boolean existsByUserAndClub(User user, Club club);

    boolean existsByUserAndClubAndClubRole(User user, Club club, ClubRole clubRole);

    Optional<Membership> findByUserAndClub(User user, Club club);

    @Query("SELECT m FROM Membership m JOIN FETCH m.club WHERE m.user.id = :userId")
    List<Membership> findByUserIdWithClub(@Param("userId") Long userId);

    @Query("SELECT m FROM Membership m JOIN FETCH m.user WHERE m.club.id = :clubId")
    List<Membership> findByClubIdWithUser(@Param("clubId") Long clubId);
}