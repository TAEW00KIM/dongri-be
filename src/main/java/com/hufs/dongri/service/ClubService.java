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
        return clubRepository.findAll()
                .stream()
                .map(club -> new ClubSummaryDto(
                        club.getId(),
                        club.getName(),
                        club.getCategory(),
                        club.getShortDescription(),
                        club.getLogoImageUrl(),
                        club.isRecruiting()
                ))
                .collect(Collectors.toList());
    }

    public ClubDetailDto getClubById(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("해당 동아리를 찾을 수 없습니다. ID: " + clubId));

        return new ClubDetailDto(
                club.getId(),
                club.getName(),
                club.getCategory(),
                club.getShortDescription(),
                club.getIntroduction(),
                club.getActivitySchedule(),
                club.getFee(),
                club.isRecruiting(),
                club.getLogoImageUrl()
        );
    }
}