// AuthService.java (수정본)
package com.hufs.dongri.service;

import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.GlobalRole;
import com.hufs.dongri.domain.enums.UserStatus;
import com.hufs.dongri.dto.auth.LoginRequestDto;
import com.hufs.dongri.dto.auth.SignUpRequestDto;
import com.hufs.dongri.dto.auth.TokenResponseDto;
import com.hufs.dongri.config.jwt.JwtTokenProvider;
import com.hufs.dongri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String HUFS_EMAIL_DOMAIN = "@hufs.ac.kr";

    // [수정] 폼 회원가입 로직
    @Transactional
    public Long signUp(SignUpRequestDto dto) {

        if (dto.getEmail().endsWith(HUFS_EMAIL_DOMAIN)) {
            throw new IllegalArgumentException("학생은 [Google로 로그인]을 이용해주세요.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setName(dto.getEmail());

        user.setGlobalRole(GlobalRole.ROLE_USER);
        user.setStatus(UserStatus.PENDING); // 상태는 '승인 대기'


        return userRepository.save(user).getId();
    }

    @Transactional
    public TokenResponseDto login(LoginRequestDto dto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String accessToken = jwtTokenProvider.generateToken(authentication);

        return new TokenResponseDto(accessToken);
    }
}