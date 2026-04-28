/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 15/04/2026
 * Usage    : Early application listener that enforces Sakila structured logging
 * Since    : Version 1.0
 */
package com.me.learning.framework.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import org.slf4j.LoggerFactory;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;



/**
 * Spring {@link ApplicationListener} that automatically configures Sakila structured
 * logging on every consumer service that includes the framework dependency.
 *
 * <p>This listener fires at {@link ApplicationEnvironmentPreparedEvent} — the earliest
 * point where the Spring {@link Environment} is available — so structured logging is
 * active before the application context is fully loaded.
 *
 * <p>It is automatically registered for all consumer services via
 * {@code META-INF/spring.factories} and requires no additional configuration.
 *
 * <h3>Default behaviour</h3>
 * <ul>
 *   <li>JSON console appender is added (enabled by default).</li>
 *   <li>Logstash TCP appender is skipped unless {@code sakila.logging.logstash-enabled=true}.</li>
 *   <li>Global JSON fields include {@code app} (from {@code spring.application.name})
 *       and {@code env} (from active Spring profiles).</li>
 * </ul>
 *
 * <h3>Consumer override</h3>
 * <pre>{@code
 * # application.yaml in the consumer service
 * sakila:
 *   logging:
 *     json-format-enabled: true       # default
 *     logstash-enabled: true          # enable Logstash shipping
 *     logstash-host: logstash-svc
 *     logstash-port: 5000
 * }</pre>
 *
 * <h3>Idempotency</h3>
 * If the JSON appender is already registered (e.g., via a consumer's own
 * {@code logback-spring.xml} that already uses {@link SakilaLogging.LogbackContextListener}),
 * this listener skips re-registration to avoid duplicate appenders.
 */
public class SakilaLoggingApplicationListener
        implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    private static final String PROP_JSON_ENABLED   = "sakila.logging.json-format-enabled";
    private static final String PROP_LOGSTASH       = "sakila.logging.logstash-enabled";
    private static final String PROP_APP_NAME       = "spring.application.name";
    private static final String PROP_ACTIVE_PROFILE = "spring.profiles.active";
    private static final String DEFAULT_APP_NAME    = "sakila-app";
    private static final String DEFAULT_PROFILE     = "default";

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Environment env = event.getEnvironment();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Skip if already configured (e.g., via consumer's logback-spring.xml)
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        if (rootLogger.getAppender(SakilaLogging.CONSOLE_APPENDER_NAME) != null) {
            return;
        }

        String fields = buildFields(env);
        boolean jsonEnabled = env.getProperty(PROP_JSON_ENABLED, Boolean.class, true);
        boolean logstashEnabled = env.getProperty(PROP_LOGSTASH, Boolean.class, false);

        if (jsonEnabled) {
            SakilaLogging.addJSONAppender(loggerContext, fields);
        }
        if (logstashEnabled) {
            SakilaLogging.addLogstashAppender(loggerContext, fields);
        }
    }

    /**
     * Builds the JSON global-fields string injected into every log event.
     * Example output: {@code {"app":"customer-svc","env":"prod"}}
     */
    private String buildFields(Environment env) {
        String appName = env.getProperty(PROP_APP_NAME, DEFAULT_APP_NAME);
        String activeProfile = env.getProperty(PROP_ACTIVE_PROFILE, DEFAULT_PROFILE);
        return String.format("{\"app\":\"%s\",\"env\":\"%s\"}", appName, activeProfile);
    }

    /**
     * Priority: runs right after Spring Boot's own logging listener.
     * {@code HIGHEST_PRECEDENCE + 20} ensures the environment is ready
     * but logging is configured as early as possible.
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 20;
    }
}

