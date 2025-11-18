package com.hufs.dongri.service;

import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.GlobalRole;
import com.hufs.dongri.domain.enums.UserStatus;
import com.hufs.dongri.dto.auth.LoginRequestDto;
import com.hufs.dongri.dto.auth.SignUpRequestDto;
import com.hufs.dongri.dto.auth.TokenResponseDto;
import com.hufs.dongri.config.jwt.JwtTokenProvider;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
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

    @Transactional
    public Long signUp(SignUpRequestDto dto) {

        if (dto.getEmail().endsWith(HUFS_EMAIL_DOMAIN)) {
            throw new CustomException(ErrorCode.STUDENT_SHOULD_USE_OAUTH);
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getEmail());
        user.setGlobalRole(GlobalRole.ROLE_USER);
        user.setStatus(UserStatus.PENDING);

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