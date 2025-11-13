package com.hufs.dongri.service;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.JoinApplication;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import com.hufs.dongri.dto.join.JoinApplicationRequestDto;
import com.hufs.dongri.global.exception.DuplicateApplicationException;
import com.hufs.dongri.global.exception.EntityNotFoundException;
import com.hufs.dongri.global.util.SecurityUtil;
import com.hufs.dongri.repository.ClubRepository;
import com.hufs.dongri.repository.JoinApplicationRepository;
import com.hufs.dongri.repository.MembershipRepository;
import com.hufs.dongri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public void applyToClub(Long clubId, JoinApplicationRequestDto dto) {
        // 1. 현재 로그인한 사용자 정보 조회
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User applicant = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("현재 사용자를 찾을 수 없습니다."));

        // 2. 신청할 동아리 정보 조회
        Club targetClub = clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("신청할 동아리를 찾을 수 없습니다."));

        // 3. [중복 검증 1] (UC-005 예외) 이미 해당 동아리의 멤버(MEMBER 또는 ADMIN)인지 확인
        if (membershipRepository.existsByUserAndClub(applicant, targetClub)) {
            throw new DuplicateApplicationException("이미 해당 동아리에 가입된 회원입니다.");
        }

        // 4. [중복 검증 2] (UC-005 예외) 이미 '승인 대기중'인 가입 신청서가 있는지 확인
        if (joinApplicationRepository.existsByUserAndClubAndStatus(applicant, targetClub, ApplicationStatus.PENDING)) {
            throw new DuplicateApplicationException("이미 처리 대기 중인 가입 신청서가 존재합니다.");
        }

        // 5. 신청서 생성
        JoinApplication application = JoinApplication.builder()
                .user(applicant)
                .club(targetClub)
                .reason(dto.getReason())
                .build();

        joinApplicationRepository.save(application);
    }
}