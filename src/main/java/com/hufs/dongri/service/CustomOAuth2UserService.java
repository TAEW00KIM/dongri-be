// CustomOAuth2UserService.java
package com.hufs.dongri.service;

import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.UserStatus;
import com.hufs.dongri.dto.oauth.CustomOAuth2User;
import com.hufs.dongri.dto.oauth.OAuthAttributes;
import com.hufs.dongri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private static final String HUFS_EMAIL_DOMAIN = "@hufs.ac.kr";

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본 OAuth2 유저 정보 로드
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        if (!attributes.getEmail().endsWith(HUFS_EMAIL_DOMAIN)) {
            log.warn("허용되지 않는 이메일 도메인입니다: {}", attributes.getEmail());
            throw new OAuth2AuthenticationException("한국외대 메일(@hufs.ac.kr)로만 로그인할 수 있습니다.");
        }

        User user = saveOrUpdate(attributes);

        // 6. SecurityContext에 저장할 CustomOAuth2User 객체 생성
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getGlobalRole().toString())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey(),
                user.getEmail(),
                user.getGlobalRole()
        );
    }

    // (참고) AuthService의 signUp과 유사하지만, OAuth2용으로 분리
    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                // 1. 이미 가입된 경우: 이름만 업데이트 (기존 권한 유지)
                .map(entity -> {
                    entity.setName(attributes.getName());
                    return entity;
                })
                // 2. 최초 가입인 경우: User 엔티티 생성
                .orElseGet(() -> {
                    User newUser = attributes.toEntity();
                    newUser.setStatus(UserStatus.ACTIVE);
                    return newUser;
                });

        return userRepository.save(user);
    }
}