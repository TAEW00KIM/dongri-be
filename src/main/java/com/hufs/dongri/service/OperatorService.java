package com.hufs.dongri.service;

import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.JoinApplication;
import com.hufs.dongri.domain.Membership;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import com.hufs.dongri.domain.enums.ClubRole;
import com.hufs.dongri.dto.application.RejectDto;
import com.hufs.dongri.dto.join.JoinApplicationDto;
import com.hufs.dongri.global.exception.EntityNotFoundException;
import com.hufs.dongri.global.util.SecurityUtil;
import com.hufs.dongri.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final JoinApplicationRepository joinApplicationRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final MembershipRepository membershipRepository;

    /**
     * (UC-007) 특정 동아리의 '대기중'인 회원 가입 신청 목록 조회
     */
    @Transactional(readOnly = true)
    public List<JoinApplicationDto> getPendingJoinApplications(Long clubId) {
        // 1. 보안 검증 및 동아리 정보 가져오기
        Club club = checkIsOperatorAndGetClub(clubId);

        // 2. PENDING 상태인 신청서 목록 조회
        List<JoinApplication> applications = joinApplicationRepository.findByClubAndStatus(club, ApplicationStatus.PENDING);

        // 3. DTO로 변환하여 반환
        return applications.stream()
                .map(JoinApplicationDto::new)
                .collect(Collectors.toList());
    }

    /**
     * (UC-007) 회원 가입 신청 '승인'
     */
    @Transactional
    public void approveJoinApplication(Long clubId, Long applicationId) {
        // 1. 보안 검증
        checkIsOperatorAndGetClub(clubId);

        // 2. 신청서 조회
        JoinApplication app = getApplicationById(applicationId);

        // 3. 상태 검증
        validateApplication(app, clubId);

        // 4. 상태 변경 (APPROVED)
        app.setStatus(ApplicationStatus.APPROVED);

        // 5. [중요] Membership 테이블에 정식 회원(ROLE_MEMBER)으로 추가
        Membership newMembership = Membership.builder()
                .user(app.getUser())
                .club(app.getClub())
                .clubRole(ClubRole.ROLE_MEMBER)
                .build();

        membershipRepository.save(newMembership);
    }

    /**
     * (UC-007) 회원 가입 신청 '거절'
     */
    @Transactional
    public void rejectJoinApplication(Long clubId, Long applicationId, RejectDto dto) {
        // 1. 보안 검증
        checkIsOperatorAndGetClub(clubId);

        // 2. 신청서 조회
        JoinApplication app = getApplicationById(applicationId);

        // 3. 상태 검증
        validateApplication(app, clubId);

        // 4. 상태 변경 (REJECTED)
        app.setStatus(ApplicationStatus.REJECTED);
        // 5. 거절 사유 저장 (MasterService의 로직과 동일하게)
        app.setReason(dto.getReason());
    }

    // --- private 헬퍼 메소드 ---

    /**
     * [보안] 현재 사용자가 해당 동아리의 운영진(ADMIN)이 맞는지 검증
     * @param clubId 검증할 동아리 ID
     * @return 검증 완료된 Club 엔티티
     */
    private Club checkIsOperatorAndGetClub(Long clubId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("현재 사용자를 찾을 수 없습니다."));

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("해당 동아리를 찾을 수 없습니다."));

        // [핵심 보안 로직]
        if (!membershipRepository.existsByUserAndClubAndClubRole(user, club, ClubRole.ROLE_ADMIN)) {
            throw new AccessDeniedException("해당 동아리의 운영진이 아닙니다.");
        }
        return club;
    }

    /**
     * 신청서 ID로 엔티티 조회
     */
    private JoinApplication getApplicationById(Long applicationId) {
        return joinApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 가입 신청서를 찾을 수 없습니다."));
    }

    /**
     * 신청서 상태 및 소유권 검증
     */
    private void validateApplication(JoinApplication app, Long clubId) {
        // 1. PENDING 상태가 아니면 처리 불가
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청서입니다.");
        }
        // 2. 다른 동아리 신청서를 처리하려는 경우
        if (!app.getClub().getId().equals(clubId)) {
            throw new AccessDeniedException("다른 동아리의 신청서를 처리할 수 없습니다.");
        }
    }
}