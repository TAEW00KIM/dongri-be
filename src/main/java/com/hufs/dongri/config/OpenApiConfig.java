// OpenApiConfig.java
package com.hufs.dongri.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "동글동글 API 명세서",
                description = "한국외대 동아리 통합 플랫폼 '동글동글' 백엔드 API 명세서입니다.",
                version = "v1.0.0"
        )
)
@SecurityScheme(
        name = "Authorization", // (SecurityConfig의 AUTHORIZATION_HEADER와 일치시킴)
        type = SecuritySchemeType.HTTP,    // HTTP 타입
        bearerFormat = "JWT",              // Bearer 토큰 형식
        scheme = "bearer",                 // 스킴은 "bearer"
        in = SecuritySchemeIn.HEADER       // 헤더에 위치함
)
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("v1-definition")
                .pathsToMatch("/api/**") // 핵심: /api/ 로 시작하는 경로만 스캔
                .build();
    }
}