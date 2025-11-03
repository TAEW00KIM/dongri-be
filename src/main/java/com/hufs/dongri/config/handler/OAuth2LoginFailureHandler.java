// OAuth2LoginFailureHandler.java
package com.hufs.dongri.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorCode = "login_failure";

        // [중요] CustomOAuth2UserService에서 던진 예외인지 확인
        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauthEx = (OAuth2AuthenticationException) exception;
            // "hufs_email_required" 에러 코드 사용
            if ("hufs_email_required".equals(oauthEx.getError().getErrorCode())) {
                errorCode = "hufs_email_required";
            }
        }

        log.warn("OAuth2 로그인 실패: {}", errorCode);

        // 4. 프론트엔드 로그인 페이지로 에러코드와 함께 리디렉션
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login") // 👈 [수정] 프론트 로그인 페이지 주소
                .queryParam("error", errorCode)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        response.sendRedirect(targetUrl);
    }
}