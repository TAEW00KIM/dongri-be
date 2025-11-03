package com.hufs.dongri.service;

import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.UserStatus;
import com.hufs.dongri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 찾을 수 없습니다: " + email));

        // -----------------------------------------------------
        // [핵심] PENDING 상태인 계정은 폼 로그인 차단
        if (user.getStatus() == UserStatus.PENDING) {
            throw new DisabledException("아직 승인 대기 중인 계정입니다.");
        }
        // -----------------------------------------------------

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getGlobalRole().toString());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}