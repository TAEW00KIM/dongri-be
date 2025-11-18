package com.hufs.dongri.repository;

import com.hufs.dongri.domain.Membership;
import com.hufs.dongri.domain.Poll;
import com.hufs.dongri.domain.PollResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PollResponseRepository extends JpaRepository<PollResponse, Long> {

    @Query("SELECT COUNT(pr) > 0 FROM PollResponse pr " +
            "WHERE pr.pollOption.poll = :poll AND pr.voter = :voter")
    boolean existsByPollAndVoter(@Param("poll") Poll poll, @Param("voter") Membership voter);

    @Query("SELECT pr.pollOption.poll.id FROM PollResponse pr WHERE pr.voter = :voter")
    Set<Long> findVotedPollIdsByVoter(@Param("voter") Membership voter);
}