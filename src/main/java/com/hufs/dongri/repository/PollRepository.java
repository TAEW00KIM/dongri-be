package com.hufs.dongri.repository;

import com.hufs.dongri.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    @Query("SELECT p FROM Poll p JOIN FETCH p.author m JOIN FETCH m.user " +
            "WHERE p.club.id = :clubId ORDER BY p.createdAt DESC")
    List<Poll> findByClubIdWithAuthor(@Param("clubId") Long clubId);
}