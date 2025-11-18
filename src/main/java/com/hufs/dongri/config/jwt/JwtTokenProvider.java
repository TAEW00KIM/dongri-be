package com.hufs.dongri.config.jwt;

import com.hufs.dongri.domain.User;
import com.hufs.dongri.dto.oauth.CustomOAuth2User;
import com.hufs.dongri.global.exception.CustomException;
import com.hufs.dongri.global.exception.code.ErrorCode;
import com.hufs.dongri.global.security.AuthenticatedUser;
import com.hufs.dongri.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenValidityInMilliseconds;
    private final UserRepository userRepository;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.token-validity-in-seconds}") long tokenValidity,
                            UserRepository userRepository) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidityInMilliseconds = tokenValidity * 1000;
        this.userRepository = userRepository;
    }

    public String generateToken(Authentication authentication) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String subject;
        Long userId;

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuth2User) {
            subject = ((CustomOAuth2User) principal).getEmail();
            userId = ((CustomOAuth2User) principal).getUserId();
        } else {
            subject = authentication.getName();
            com.hufs.dongri.domain.User user = userRepository.findByEmail(subject)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            userId = user.getId(); 
        }

        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", authorities)
                .claim("userId", userId)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("roles").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();

        AuthenticatedUser principal = new AuthenticatedUser(userId, email, authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}