package com.hufs.dongri.service;

import com.hufs.dongri.domain.*;
import com.hufs.dongri.domain.enums.ApplicationStatus;
import com.hufs.dongri.domain.enums.ClubRole;
import com.hufs.dongri.domain.enums.NoticeType;
import com.hufs.dongri.dto.application.RejectDto;
import com.hufs.dongri.dto.join.JoinApplicationDto;
import com.hufs.dongri.dto.notice.NoticeRequestDto;
import com.hufs.dongri.dto.operator.AttendanceCheckRequest;
import com.hufs.dongri.dto.operator.FeeStatusUpdateRequest;
import com.hufs.dongri.dto.operator.OperatorMemberResponse;
import com.hufs.dongri.dto.operator.PollCreateRequest;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final JoinApplicationRepository joinApplicationRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final MembershipRepository membershipRepository;
    private final NoticeRepository noticeRepository;
    private final AttendanceRepository attendanceRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    public List<OperatorMemberResponse> getClubMembers(Long userId, Long clubId) {
        checkIsOperatorAndGetClub(userId, clubId);

        List<Membership> members = membershipRepository.findByClubIdWithUser(clubId);

        return members.stream()
                .map(OperatorMemberResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JoinApplicationDto> getPendingJoinApplications(Long userId, Long clubId) {
        Club club = checkIsOperatorAndGetClub(userId, clubId);

        List<JoinApplication> applications = joinApplicationRepository.findByClubAndStatus(club, ApplicationStatus.PENDING);

        return applications.stream()
                .map(JoinApplicationDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveJoinApplication(Long userId, Long clubId, Long applicationId) {
        checkIsOperatorAndGetClub(userId, clubId);
        JoinApplication app = getApplicationById(applicationId);
        validateApplication(app, clubId);

        app.setStatus(ApplicationStatus.APPROVED);

        Membership newMembership = Membership.builder()
                .user(app.getUser())
                .club(app.getClub())
                .clubRole(ClubRole.ROLE_MEMBER)
                .build();

        membershipRepository.save(newMembership);
    }

    @Transactional
    public void rejectJoinApplication(Long userId, Long clubId, Long applicationId, RejectDto dto) {
        checkIsOperatorAndGetClub(userId, clubId);
        JoinApplication app = getApplicationById(applicationId);
        validateApplication(app, clubId);

        app.setStatus(ApplicationStatus.REJECTED);
        app.setReason(dto.getReason());
    }

    @Transactional
    public void createNotice(Long userId, Long clubId, NoticeRequestDto dto) {
        Club club = checkIsOperatorAndGetClub(userId, clubId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Membership authorMembership = membershipRepository.findByUserAndClub(user, club)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_OPERATOR));

        Notice notice = Notice.builder()
                .club(club)
                .author(authorMembership)
                .title(dto.getTitle())
                .content(dto.getContent())
                .type(dto.getType())
                .eventDate(dto.getEventDate())
                .build();

        noticeRepository.save(notice);
    }

    @Transactional
    public void checkAttendance(Long userId, Long clubId, Long noticeId, AttendanceCheckRequest dto) {
        Club club = checkIsOperatorAndGetClub(userId, clubId);

        Membership membership = membershipRepository.findById(dto.getMembershipId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        if (!membership.getClub().getId().equals(clubId)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_IN_CLUB);
        }

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        if (!notice.getClub().getId().equals(clubId)) {
            throw new CustomException(ErrorCode.NOTICE_NOT_IN_CLUB);
        }

        if (notice.getType() != NoticeType.EVENT) {
            throw new CustomException(ErrorCode.NOTICE_IS_NOT_EVENT);
        }

        Attendance attendance = attendanceRepository.findByMembershipAndNotice(membership, notice)
                .orElseGet(() -> Attendance.builder()
                        .membership(membership)
                        .notice(notice)
                        .build());

        attendance.setStatus(dto.getStatus());
        attendanceRepository.save(attendance);
    }

    @Transactional
    public void updateFeeStatus(Long userId, Long clubId, Long paymentId, FeeStatusUpdateRequest dto) {
        checkIsOperatorAndGetClub(userId, clubId);

        FeePayment feePayment = feePaymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEE_PAYMENT_NOT_FOUND));

        if (!feePayment.getMembership().getClub().getId().equals(clubId)) {
            throw new CustomException(ErrorCode.FEE_PAYMENT_NOT_IN_CLUB);
        }

        feePayment.setStatus(dto.getStatus());
        feePaymentRepository.save(feePayment);
    }

    @Transactional
    public void createPoll(Long userId, Long clubId, PollCreateRequest dto) {
        Club club = checkIsOperatorAndGetClub(userId, clubId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Membership authorMembership = membershipRepository.findByUserAndClub(user, club)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_CLUB_OPERATOR));

        if (dto.getOptionTexts() == null || dto.getOptionTexts().size() < 2) {
            throw new CustomException(ErrorCode.POLL_REQUIRES_MIN_TWO_OPTIONS);
        }
        if (dto.getDeadline() != null && dto.getDeadline().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.POLL_DEADLINE_IN_PAST);
        }

        Poll poll = Poll.builder()
                .club(club)
                .author(authorMembership)
                .title(dto.getTitle())
                .deadline(dto.getDeadline())
                .isAnonymous(dto.getIsAnonymous())
                .build();

        List<PollOption> options = dto.getOptionTexts().stream()
                .map(optionText -> PollOption.builder()
                        .poll(poll) // 연관관계 설정
                        .optionText(optionText)
                        .build())
                .collect(Collectors.toList());

        poll.setOptions(options);

        pollRepository.save(poll);
    }

    private Club checkIsOperatorAndGetClub(Long userId, Long clubId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        if (!membershipRepository.existsByUserAndClubAndClubRole(user, club, ClubRole.ROLE_ADMIN)) {
            throw new CustomException(ErrorCode.NOT_CLUB_OPERATOR);
        }
        return club;
    }

    private JoinApplication getApplicationById(Long applicationId) {
        return joinApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    private void validateApplication(JoinApplication app, Long clubId) {
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new CustomException(ErrorCode.APPLICATION_ALREADY_PROCESSED);
        }
        if (!app.getClub().getId().equals(clubId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}