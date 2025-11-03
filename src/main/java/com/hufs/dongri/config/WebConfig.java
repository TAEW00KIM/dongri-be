// WebConfig.java
package com.hufs.dongri.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 1. /api/** 경로에 대해서
                .allowedOrigins("http://localhost:3000") // 2. [중요] 프론트엔드 주소 (http://localhost:3000)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 3. 허용할 HTTP 메서드
                .allowedHeaders("*") // 4. 모든 헤더 허용
                .allowCredentials(true) // 5. 쿠키/인증 헤더 허용
                .maxAge(3600); // 6. pre-flight 요청 캐시 시간 (1시간)
    }
}