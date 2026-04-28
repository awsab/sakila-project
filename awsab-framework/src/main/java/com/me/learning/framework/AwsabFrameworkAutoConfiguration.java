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
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.me.learning.framework.logging.AwsabLoggingProperties;
import com.me.learning.framework.web.config.AwsabLoggingInterceptor;
import com.me.learning.framework.web.config.AwsabWebMvcConfigurer;
import com.me.learning.framework.web.errors.GlobalExceptionHandler;
import com.me.learning.framework.web.util.LinkHeaderUtil;

/**
 * Spring Boot auto-configuration for the AWSAB shared framework.
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
@EnableConfigurationProperties (AwsabLoggingProperties.class)
@ConditionalOnWebApplication (type = ConditionalOnWebApplication.Type.SERVLET)
public class AwsabFrameworkAutoConfiguration {

    public AwsabFrameworkAutoConfiguration () {
        log.info ("AWSAB Framework auto-configuration loaded — all framework beans registered");
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
     * Java 8 time module for Jackson
     */
    @Bean
    @ConditionalOnMissingBean (JavaTimeModule.class)
    public JavaTimeModule javaTimeModule () {
        log.debug ("Registering Jackson JavaTimeModule");
        return new JavaTimeModule ();
    }

    /**
     * Java 8 optional type module for Jackson.
     */
    @Bean
    @ConditionalOnMissingBean (Jdk8Module.class)
    public Jdk8Module jdk8Module () {
        log.debug ("Registering Jackson Jdk8Module");
        return new Jdk8Module ();
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
     */
    @Bean
    @ConditionalOnMissingBean (Jackson2ObjectMapperBuilderCustomizer.class)
    public Jackson2ObjectMapperBuilderCustomizer jacksonDefaultsCustomizer () {
        return builder -> builder.featuresToDisable (
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                SerializationFeature.FAIL_ON_EMPTY_BEANS,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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
    @ConditionalOnMissingBean (AwsabLoggingInterceptor.class)
    public AwsabLoggingInterceptor awsabLoggingInterceptor () {
        log.debug ("Registering AwsabLoggingInterceptor");
        return new AwsabLoggingInterceptor ();
    }

    /**
     * Shared WebMvc configurer: registers the logging interceptor on /api/**
     * and applies default CORS settings for all services.
     * Override in a child service by declaring your own {@code WebMvcConfigurer} bean.
     */
    @Bean
    @ConditionalOnMissingBean (AwsabWebMvcConfigurer.class)
    public AwsabWebMvcConfigurer awsabWebMvcConfigurer (AwsabLoggingInterceptor loggingInterceptor) {
        log.debug ("Registering AwsabWebMvcConfigurer");
        return new AwsabWebMvcConfigurer (loggingInterceptor);
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

    /**
     * Default converter adapter used by reactive condition builder utilities.
     */
    /*@Bean
    @ConditionalOnMissingBean(ColumnConverterReactive.class)
    public ColumnConverterReactive columnConverterReactive(ConversionService conversionService) {
        log.debug("Registering ColumnConverterReactive");
        return new ColumnConverterReactive() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T convert(Object value, Class<T> target) {
                if (target == null) {
                    return (T) value;
                }
                return conversionService.convert(value, target);
            }
        };
    }*/
}
