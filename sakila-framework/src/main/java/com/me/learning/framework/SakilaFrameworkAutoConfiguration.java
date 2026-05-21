/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 14/04/2026
 * Usage    :
 * Since    : Version 1.0
 */
package com.me.learning.framework;

import java.time.Clock;
import java.time.ZoneOffset;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

import com.me.learning.framework.logging.SakilaLoggingProperties;
import com.me.learning.framework.web.config.SakilaLoggingInterceptor;
import com.me.learning.framework.web.config.SakilaWebMvcConfigurer;
import com.me.learning.framework.web.errors.GlobalExceptionHandler;
import com.me.learning.framework.web.util.LinkHeaderUtil;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;


/**
 * Spring Boot auto-configuration for the Sakila shared framework.
 *
 * <p>This class is loaded through Spring Boot's
 * auto-configuration mechanism when the framework JAR is on the classpath,
 * registered via:
 * {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}
 * <p>The library intentionally avoids component scanning and only exposes
 * narrowly scoped beans that are safe to share across services.
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties (SakilaLoggingProperties.class)
@ConditionalOnWebApplication (type = ConditionalOnWebApplication.Type.SERVLET)
public class SakilaFrameworkAutoConfiguration {

    public SakilaFrameworkAutoConfiguration () {
        log.info ("Sakila Framework auto-configuration loaded — all framework beans registered");
    }

    /**
     * Global {@code @RestControllerAdvice} that maps every exception
     * to the standard framework API response envelope
     * envelope with the appropriate HTTP status.
     */
    @Bean
    @ConditionalOnMissingBean (GlobalExceptionHandler.class)
    public GlobalExceptionHandler globalExceptionHandler () {
        log.debug ("Registering GlobalExceptionHandler");
        return new GlobalExceptionHandler ();
    }

    /**
     * Enterprise Jackson configuration.
     *
     * <p>Defaults applied to every service:
     * <ul>
     *   <li>Java 8 date/time types serialised as ISO-8601 strings (not timestamps)</li>
     *   <li>Unknown JSON properties ignored on deserialization (forward compatibility)</li>
     *   <li>Empty beans do not cause serialisation failure</li>
     *   <li>Null values included in output (explicit over implicit)</li>
     * </ul>
     *
     * <p>NOTE: Jackson 3.x (used by Spring Boot 4.x) integrates JavaTimeModule and Jdk8Module
     * natively into jackson-databind core — no separate module registration required.
     */
    @Bean
    @ConditionalOnMissingBean (JsonMapperBuilderCustomizer.class)
    public JsonMapperBuilderCustomizer jacksonDefaultsCustomizer () {
        return builder -> builder
                .disable (DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable (SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Framework-wide clock, pinned to UTC for consistent time handling.
     */
    @Bean
    @ConditionalOnMissingBean (Clock.class)
    public Clock frameworkClock () {
        log.debug ("Registering framework Clock (UTC)");
        return Clock.system (ZoneOffset.UTC);
    }

    /**
     * Shared HTTP request/response logging interceptor.
     * Logs URI, method, status, and execution time at appropriate log levels.
     */
    @Bean
    @ConditionalOnMissingBean (SakilaLoggingInterceptor.class)
    public SakilaLoggingInterceptor sakilaLoggingInterceptor () {
        log.debug ("Registering SakilaLoggingInterceptor");
        return new SakilaLoggingInterceptor ();
    }

    /**
     * Shared WebMvc configurer: registers the logging interceptor on /api/**
     * and applies default CORS settings for all services.
     * Override in a child service by declaring your own {@code WebMvcConfigurer} bean.
     */
    @Bean
    @ConditionalOnMissingBean (SakilaWebMvcConfigurer.class)
    public SakilaWebMvcConfigurer sakilaWebMvcConfigurer (SakilaLoggingInterceptor loggingInterceptor) {
        log.debug ("Registering SakilaWebMvcConfigurer");
        return new SakilaWebMvcConfigurer (loggingInterceptor);
    }

    /**
     * Shared utility for RFC5988 pagination link header generation.
     */
    @Bean
    @ConditionalOnMissingBean (LinkHeaderUtil.class)
    public LinkHeaderUtil linkHeaderUtil () {
        log.debug ("Registering LinkHeaderUtil");
        return new LinkHeaderUtil ();
    }
}

