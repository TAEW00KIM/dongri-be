package com.hufs.dongri.service;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.dto.club.ClubDetailDto;
import com.hufs.dongri.dto.club.ClubSummaryDto;
import com.hufs.dongri.global.exception.EntityNotFoundException;
import com.hufs.dongri.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;

    public List<ClubSummaryDto> getAllClubs() {
        // 1. DB에서 모든 Club을 찾아서
        return clubRepository.findAll()
                .stream()
                // 2. ClubSummaryDto로 변환 (생성자 매핑)
                .map(ClubSummaryDto::new)
                // 3. 리스트로 반환
                .collect(Collectors.toList());
    }

    public ClubDetailDto getClubById(Long clubId) {
        // 1. DB에서 ID로 Club을 찾음
        Club club = clubRepository.findById(clubId)
                // 2. 없으면 404 에러 (EntityNotFoundException 사용)
                .orElseThrow(() -> new EntityNotFoundException("해당 동아리를 찾을 수 없습니다. ID: " + clubId));

        // 3. ClubDetailDto로 변환하여 반환
        return new ClubDetailDto(club);
    }
}