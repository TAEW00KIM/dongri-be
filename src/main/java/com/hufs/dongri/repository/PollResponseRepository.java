package com.hufs.dongri.repository;

import com.hufs.dongri.domain.PollResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollResponseRepository extends JpaRepository<PollResponse, Long> {
}