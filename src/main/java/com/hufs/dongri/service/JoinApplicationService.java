package com.hufs.dongri.service;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.JoinApplication;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import com.hufs.dongri.dto.join.JoinApplicationRequestDto;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.repository.ClubRepository;
import com.hufs.dongri.repository.JoinApplicationRepository;
import com.hufs.dongri.repository.MembershipRepository;
import com.hufs.dongri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JoinApplicationService {

    private final JoinApplicationRepository joinApplicationRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public void applyToClub(Long userId, Long clubId, JoinApplicationRequestDto dto) {
        User applicant = userRepository.findById(userId) // (수정) ID로 사용자 조회
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Club targetClub = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        if (membershipRepository.existsByUserAndClub(applicant, targetClub)) {
            throw new CustomException(ErrorCode.ALREADY_MEMBER);
        }

        if (joinApplicationRepository.existsByUserAndClubAndStatus(applicant, targetClub, ApplicationStatus.PENDING)) {
            throw new CustomException(ErrorCode.APPLICATION_ALREADY_PENDING);
        }

        JoinApplication application = JoinApplication.builder()
                .user(applicant)
                .club(targetClub)
                .reason(dto.getReason())
                .build();

        joinApplicationRepository.save(application);
    }
}