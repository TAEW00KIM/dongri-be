package com.hufs.dongri.service;

import com.hufs.dongri.domain.User;
import com.hufs.dongri.dto.user.UserDetailRequestDto;
import com.hufs.dongri.global.util.SecurityUtil;
import com.hufs.dongri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void updateUserDetails(UserDetailRequestDto dto) {
        // 1. 현재 로그인한 사용자 조회
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("현재 사용자를 찾을 수 없습니다."));

        // 2. [검증] 학번 중복 체크
        if (userRepository.existsByStudentId(dto.getStudentId())) {
            // (선택적) 이미 내 학번이면 통과시키는 로직
            if (!user.getStudentId().equals(dto.getStudentId())) {
                throw new IllegalArgumentException("이미 등록된 학번입니다.");
            }
        }

        // 3. 정보 업데이트 (Dirty Checking)
        user.setStudentId(dto.getStudentId());
        user.setMajor(dto.getMajor());

        // @Transactional이 종료되면서 자동 save
    }
}