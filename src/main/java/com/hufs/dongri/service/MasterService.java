// MasterService.java
package com.hufs.dongri.service;

import com.hufs.dongri.domain.AdminApplication;
import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.Membership;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import com.hufs.dongri.domain.enums.ClubRole;
import com.hufs.dongri.domain.enums.UserStatus;
import com.hufs.dongri.dto.application.ApplicationDto;
import com.hufs.dongri.dto.application.RejectDto;
import com.hufs.dongri.dto.master.PendingUserDto;
import com.hufs.dongri.global.exception.EntityNotFoundException;
import com.hufs.dongri.repository.AdminApplicationRepository;
import com.hufs.dongri.repository.ClubRepository;
import com.hufs.dongri.repository.MembershipRepository;
import com.hufs.dongri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterService {

    private final AdminApplicationRepository adminApplicationRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    @Transactional(readOnly = true)
    public List<ApplicationDto> getPendingApplications() {
        // 1. '대기중'인 모든 신청서를 조회
        List<AdminApplication> applications = adminApplicationRepository.findByStatus(ApplicationStatus.PENDING);

        // 2. DTO 리스트로 변환하여 반환
        return applications.stream()
                .map(ApplicationDto::new) // DTO의 생성자(AdminApplication app) 사용
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveApplication(Long applicationId) {
        // 1. 신청서 조회
        AdminApplication app = adminApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("신청서를 찾을 수 없습니다."));

        // 2. 상태가 'PENDING'이 아니면 오류
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청서입니다.");
        }

        // 3. 신청서 상태 'APPROVED'로 변경
        app.setStatus(ApplicationStatus.APPROVED);
        // (JPA 트랜잭션 종료 시 자동 dirty checking으로 update)

        // 4. [핵심] Membership 테이블에 ROLE_ADMIN으로 추가
        Membership newAdminMembership = new Membership();
        newAdminMembership.setUser(app.getApplicant());
        newAdminMembership.setClub(app.getTargetClub());
        newAdminMembership.setClubRole(ClubRole.ROLE_ADMIN); // 'ADMIN' 권한 부여

        membershipRepository.save(newAdminMembership);
    }

    @Transactional
    public void rejectApplication(Long applicationId, RejectDto dto) {
        // 1. 신청서 조회
        AdminApplication app = adminApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("신청서를 찾을 수 없습니다."));

        // 2. 상태가 'PENDING'이 아니면 오류
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청서입니다.");
        }

        // 3. 신청서 상태 'REJECTED'로 변경
        app.setStatus(ApplicationStatus.REJECTED);
        app.setReason(dto.getReason()); // 거절 사유 저장
    }

    @Transactional(readOnly = true)
    public List<PendingUserDto> getPendingAccounts() {
        // 1. PENDING 상태인 계정 목록 조회
        List<User> pendingUsers = userRepository.findByStatus(UserStatus.PENDING);
        // 2. DTO로 변환
        return pendingUsers.stream()
                .map(PendingUserDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveClubAccount(Long userId, Long clubId) {
        // 1. PENDING 상태인 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 계정입니다.");
        }

        // 2. ADMIN으로 임명할 동아리 조회
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("해당 동아리를 찾을 수 없습니다."));

        // 3. 계정 상태를 ACTIVE로 변경
        user.setStatus(UserStatus.ACTIVE);

        // 4. 해당 동아리의 ADMIN으로 즉시 임명
        Membership newAdminMembership = new Membership();
        newAdminMembership.setUser(user);
        newAdminMembership.setClub(club);
        newAdminMembership.setClubRole(ClubRole.ROLE_ADMIN);

        membershipRepository.save(newAdminMembership);
    }
}