// SecurityUtil.java
package com.hufs.dongri.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SecurityUtil {

    // SecurityContext에서 현재 인증된 사용자의 이메일을 가져옵니다.
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new UsernameNotFoundException("Security Context에 인증 정보가 없습니다.");
        }

        return authentication.getName();
    }
}