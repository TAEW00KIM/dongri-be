package com.hufs.dongri.service;

import com.hufs.dongri.domain.Membership;
import com.hufs.dongri.domain.User;
import com.hufs.dongri.dto.user.UserDetailRequestDto;
import com.hufs.dongri.dto.user.UserMyClubResponse;
import com.hufs.dongri.dto.user.UserMyInfoResponse;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.repository.MembershipRepository;
import com.hufs.dongri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    public UserMyInfoResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserMyInfoResponse.from(user);
    }

    public List<UserMyClubResponse> getMyClubs(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<Membership> memberships = membershipRepository.findByUserIdWithClub(userId);

        return memberships.stream()
                .map(UserMyClubResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserDetails(Long userId, UserDetailRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getStudentId() == null || !user.getStudentId().equals(dto.getStudentId())) {
            if (userRepository.existsByStudentId(dto.getStudentId())) {
                throw new CustomException(ErrorCode.STUDENT_ID_DUPLICATED);
            }
        }

        user.setStudentId(dto.getStudentId());
        user.setMajor(dto.getMajor());
    }
}