package com.hufs.dongri.service;

import com.hufs.dongri.domain.User;
import com.hufs.dongri.dto.user.UserDetailRequestDto;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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