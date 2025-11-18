package com.hufs.dongri.service;

import com.hufs.dongri.domain.AdminApplication;
import com.hufs.dongri.domain.Club;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import com.hufs.dongri.dto.application.ApplicationRequestDto;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.repository.AdminApplicationRepository;
import com.hufs.dongri.repository.ClubRepository;
import com.hufs.dongri.repository.MembershipRepository;
import com.hufs.dongri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final AdminApplicationRepository adminApplicationRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public void applyForAdmin(Long userId, ApplicationRequestDto dto) {
        User applicant = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Club targetClub = clubRepository.findById(dto.getClubId())
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        if (adminApplicationRepository.existsByApplicantAndStatus(applicant, ApplicationStatus.PENDING)) {
            throw new CustomException(ErrorCode.APPLICATION_ALREADY_PENDING);
        }

        if (membershipRepository.existsByUserAndClub(applicant, targetClub)) {
            throw new CustomException(ErrorCode.ALREADY_MEMBER);
        }

        AdminApplication application = new AdminApplication();
        application.setApplicant(applicant);
        application.setTargetClub(targetClub);
        application.setReason(dto.getReason());
        application.setStatus(ApplicationStatus.PENDING);

        adminApplicationRepository.save(application);
    }
}