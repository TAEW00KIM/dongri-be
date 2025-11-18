package com.hufs.dongri.repository;

import com.hufs.dongri.domain.Notice;
import com.hufs.dongri.domain.enums.NoticeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query("SELECT n FROM Notice n JOIN FETCH n.club " +
            "WHERE n.club.id IN :clubIds AND n.type = :type " +
            "ORDER BY n.eventDate ASC")
    List<Notice> findByClubIdInAndTypeWithClub(
            @Param("clubIds") List<Long> clubIds,
            @Param("type") NoticeType type
    );
}