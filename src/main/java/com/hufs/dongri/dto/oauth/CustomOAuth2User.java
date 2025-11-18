package com.hufs.dongri.dto.oauth;

import com.hufs.dongri.domain.enums.GlobalRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private String email;
    private GlobalRole globalRole;
    private Long userId;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            String email, GlobalRole globalRole,
                            Long userId) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.globalRole = globalRole;
        this.userId = userId;
    }
}