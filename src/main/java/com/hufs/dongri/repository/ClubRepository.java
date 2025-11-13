package com.hufs.dongri.repository;

import com.hufs.dongri.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClubRepository extends JpaRepository<Club, Long>, JpaSpecificationExecutor<Club> {

}
