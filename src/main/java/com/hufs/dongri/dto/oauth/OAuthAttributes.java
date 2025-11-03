// OAuthAttributes.java (제공자별 응답을 파싱)
package com.hufs.dongri.dto.oauth;

import com.hufs.dongri.domain.User;
import com.hufs.dongri.domain.enums.GlobalRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    // 1. 제공자별로 attributes를 파싱하는 정적 팩토리 메서드
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        // (향후 Naver, Kakao 추가 시 여기에 'naver', 'kakao' 분기 추가)
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // 2. OAuth2 유저 정보를 바탕으로 User 엔티티 생성 (신규 가입 시)
    public User toEntity() {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        // OAuth2 사용자는 비밀번호 로그인을 사용하지 않으므로, 임의의 값으로 채움
        user.setPassword(UUID.randomUUID().toString());
        user.setGlobalRole(GlobalRole.ROLE_USER); // 기본 역할은 USER

        // (주의!) OAuth2 최초 가입 시 학번/학과는 null이 됩니다.
        // 추가 정보 입력 페이지로 유도하거나, null을 허용해야 합니다.
        // 여기서는 User 엔티티의 studentId, major가 nullable하다고 가정합니다.

        return user;
    }
}