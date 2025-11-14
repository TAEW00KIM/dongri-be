package com.hufs.dongri.repository;

import com.hufs.dongri.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // (나중에 5순위에서 공지 목록 조회 시 여기에 쿼리 추가)
}