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
        List<AdminApplication> applications = adminApplicationRepository.findByStatus(ApplicationStatus.PENDING);

        return applications.stream()
                .map(app -> new ApplicationDto(
                        app.getId(),
                        app.getStatus(),
                        app.getReason(),
                        app.getApplicant().getId(),
                        app.getApplicant().getName(),
                        app.getApplicant().getStudentId(),
                        app.getTargetClub().getId(),
                        app.getTargetClub().getName()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveApplication(Long applicationId) {
        AdminApplication app = adminApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("신청서를 찾을 수 없습니다."));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청서입니다.");
        }

        app.setStatus(ApplicationStatus.APPROVED);

        Membership newAdminMembership = new Membership();
        newAdminMembership.setUser(app.getApplicant());
        newAdminMembership.setClub(app.getTargetClub());
        newAdminMembership.setClubRole(ClubRole.ROLE_ADMIN);

        membershipRepository.save(newAdminMembership);
    }

    @Transactional
    public void rejectApplication(Long applicationId, RejectDto dto) {
        AdminApplication app = adminApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("신청서를 찾을 수 없습니다."));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청서입니다.");
        }

        app.setStatus(ApplicationStatus.REJECTED);
        app.setReason(dto.getReason());
    }

    @Transactional(readOnly = true)
    public List<PendingUserDto> getPendingAccounts() {
        List<User> pendingUsers = userRepository.findByStatus(UserStatus.PENDING);

        return pendingUsers.stream()
                .map(user -> new PendingUserDto(
                        user.getId(),
                        user.getEmail(),
                        user.getName()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveClubAccount(Long userId, Long clubId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 계정입니다.");
        }

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("해당 동아리를 찾을 수 없습니다."));

        user.setStatus(UserStatus.ACTIVE);

        Membership newAdminMembership = new Membership();
        newAdminMembership.setUser(user);
        newAdminMembership.setClub(club);
        newAdminMembership.setClubRole(ClubRole.ROLE_ADMIN);

        membershipRepository.save(newAdminMembership);
    }
}