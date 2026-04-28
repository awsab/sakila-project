package com.me.learning.framework.web.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : Shared WebMvc configuration provided by sakila-framework.
 *            Registers {@link SakilaLoggingInterceptor} for /api/** and applies
 *            default CORS settings.  Child services do NOT need their own WebMvcConfigurer
 *            unless they need to override these defaults — use @ConditionalOnMissingBean.
 * Since    : Version 1.0
 */
@RequiredArgsConstructor
public class SakilaWebMvcConfigurer implements WebMvcConfigurer {

    private final SakilaLoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors (InterceptorRegistry registry) {
        registry.addInterceptor (loggingInterceptor)
                .addPathPatterns ("/api/**")
                .excludePathPatterns ("/api/v1/actuator/**", "/swagger-ui/**", "/v3/api-docs/**");
    }

    @Override
    public void addCorsMappings (CorsRegistry registry) {
        registry.addMapping ("/api/**")
                .allowedOrigins ("*")
                .allowedMethods ("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders ("*")
                .allowCredentials (false)
                .maxAge (3600);
    }
}

