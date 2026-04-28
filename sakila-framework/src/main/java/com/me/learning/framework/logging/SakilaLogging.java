/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 02/01/2025
 * Usage    : Structured logging configuration utilities (JSON + Logstash appenders)
 * Since    : Version 1.0
 */
package com.me.learning.framework.logging;

import java.net.InetSocketAddress;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.spi.ContextAwareBase;

import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.composite.ContextJsonProvider;
import net.logstash.logback.composite.GlobalCustomFieldsJsonProvider;
import net.logstash.logback.composite.loggingevent.ArgumentsJsonProvider;
import net.logstash.logback.composite.loggingevent.LogLevelJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggerNameJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventFormattedTimestampJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders;
import net.logstash.logback.composite.loggingevent.LoggingEventPatternJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventThreadNameJsonProvider;
import net.logstash.logback.composite.loggingevent.MdcJsonProvider;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.composite.loggingevent.StackTraceJsonProvider;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class providing programmatic Logback appender configuration for structured logging.
 *
 * <p>Two appenders are supported:
 * <ul>
 *   <li><strong>JSON console appender</strong> — writes structured JSON to stdout,
 *       suitable for log aggregation (e.g., Loki/Grafana).</li>
 *   <li><strong>Logstash TCP appender</strong> — ships logs to a Logstash/OpenSearch pipeline.</li>
 * </ul>
 *
 * <p>Consumers call the static methods from a Logback {@code LoggerContextListener}
 * (e.g., inside a {@code logback-spring.xml} {@code <configuration>} block or programmatically).
 */
@Slf4j
public final class SakilaLogging {

    /** Name of the JSON console appender — exposed so listeners can check for duplicates. */
    public static final String CONSOLE_APPENDER_NAME = "CONSOLE_APPENDER";

    /** Name of the Logstash TCP appender — exposed so listeners can check for duplicates. */
    public static final String LOGSTASH_APPENDER_NAME = "LOGSTASH_APPENDER";

    private SakilaLogging() {
    }

    /**
     * Attaches a structured JSON {@link ConsoleAppender} to the root logger.
     *
     * @param loggerContext the active Logback context
     * @param fields        JSON string of extra global fields, e.g. {@code {"app":"my-svc"}}
     */
    public static void addJSONAppender(LoggerContext loggerContext, String fields) {
        log.info("Adding JSON console appender");

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setEncoder(buildCompositeJsonEncoder(loggerContext, fields));
        consoleAppender.setName(CONSOLE_APPENDER_NAME);
        consoleAppender.start();

        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAppender(CONSOLE_APPENDER_NAME);
        rootLogger.addAppender(consoleAppender);
    }

    /**
     * Attaches a {@link LogstashTcpSocketAppender} that ships logs to {@code localhost:5000}.
     *
     * @param loggerContext the active Logback context
     * @param fields        JSON string of extra global fields
     */
    public static void addLogstashAppender(LoggerContext loggerContext, String fields) {
        log.info("Adding Logstash TCP appender");

        LogstashTcpSocketAppender logstashAppender = new LogstashTcpSocketAppender();
        logstashAppender.setName(LOGSTASH_APPENDER_NAME);
        logstashAppender.addDestinations(new InetSocketAddress("localhost", 5000));
        logstashAppender.setContext(loggerContext);
        logstashAppender.setEncoder(buildLogstashEncoder(fields));
        logstashAppender.start();

        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(logstashAppender);
    }

    // ── Private factory helpers ─────────────────────────────────────────────────

    private static LoggingEventCompositeJsonEncoder buildCompositeJsonEncoder(
            LoggerContext loggerContext, String fields) {
        LoggingEventCompositeJsonEncoder encoder = new LoggingEventCompositeJsonEncoder();
        encoder.setContext(loggerContext);
        encoder.setProviders(buildJsonProviders(loggerContext, fields));
        encoder.start();
        return encoder;
    }

    private static LoggingEventJsonProviders buildJsonProviders(
            LoggerContext context, String customFields) {
        LoggingEventJsonProviders providers = new LoggingEventJsonProviders();
        providers.addArguments(new ArgumentsJsonProvider());
        providers.addContext(new ContextJsonProvider<>());
        providers.addGlobalCustomFields(buildCustomFieldsProvider(customFields));
        providers.addLogLevel(new LogLevelJsonProvider());
        providers.addLoggerName(buildLoggerNameProvider());
        providers.addMdc(new MdcJsonProvider());
        providers.addMessage(new MessageJsonProvider());
        providers.addPattern(new LoggingEventPatternJsonProvider());
        providers.addStackTrace(buildStackTraceProvider());
        providers.addThreadName(new LoggingEventThreadNameJsonProvider());
        providers.addTimestamp(buildTimestampProvider());
        providers.setContext(context);
        return providers;
    }

    private static GlobalCustomFieldsJsonProvider<ILoggingEvent> buildCustomFieldsProvider(
            String fields) {
        GlobalCustomFieldsJsonProvider<ILoggingEvent> provider = new GlobalCustomFieldsJsonProvider<>();
        provider.setCustomFields(fields);
        return provider;
    }

    private static LoggerNameJsonProvider buildLoggerNameProvider() {
        LoggerNameJsonProvider provider = new LoggerNameJsonProvider();
        provider.setShortenedLoggerNameLength(20);
        return provider;
    }

    private static StackTraceJsonProvider buildStackTraceProvider() {
        StackTraceJsonProvider provider = new StackTraceJsonProvider();
        provider.setThrowableConverter(buildThrowableConverter());
        return provider;
    }

    private static LoggingEventFormattedTimestampJsonProvider buildTimestampProvider() {
        LoggingEventFormattedTimestampJsonProvider provider =
                new LoggingEventFormattedTimestampJsonProvider();
        provider.setTimeZone("UTC");
        provider.setFieldName("@timestamp");
        return provider;
    }

    private static ThrowableHandlingConverter buildThrowableConverter() {
        ShortenedThrowableConverter converter = new ShortenedThrowableConverter();
        converter.setRootCauseFirst(true);
        return converter;
    }

    private static LogstashEncoder buildLogstashEncoder(String fields) {
        LogstashEncoder encoder = new LogstashEncoder();
        encoder.setThrowableConverter(buildThrowableConverter());
        encoder.setCustomFields(fields);
        return encoder;
    }

    // ── Inner listener ──────────────────────────────────────────────────────────

    /**
     * A reset-resistant {@link LoggerContextListener} that re-attaches appenders
     * after a Logback context reset (e.g., Spring profile activation).
     *
     * <p>Supports both programmatic construction and XML setter-based injection
     * from {@code logback-spring.xml}:
     * <pre>{@code
     * <contextListener class="com.me.learning.framework.logging.SakilaLogging$LogbackContextListener">
     *     <fields>{"app":"my-svc","env":"prod"}</fields>
     *     <jsonFormatEnabled>true</jsonFormatEnabled>
     *     <logstashEnabled>false</logstashEnabled>
     * </contextListener>
     * }</pre>
     */
    public static class LogbackContextListener extends ContextAwareBase
            implements LoggerContextListener {

        private String fields;
        private boolean jsonFormatEnabled;
        private boolean logstashEnabled;

        /**
         * No-arg constructor for XML-based configuration (logback-spring.xml setter injection).
         * Defaults: JSON enabled, Logstash disabled, empty custom fields.
         */
        public LogbackContextListener() {
            this("{}", true, false);
        }

        /**
         * Creates a listener that enables only the JSON console appender.
         *
         * @param fields global custom JSON fields string
         */
        public LogbackContextListener(String fields) {
            this(fields, true, false);
        }

        /**
         * Creates a listener with explicit appender configuration.
         *
         * @param fields           global custom JSON fields string
         * @param jsonFormatEnabled whether to attach the JSON console appender
         * @param logstashEnabled   whether to attach the Logstash TCP appender
         */
        public LogbackContextListener(String fields, boolean jsonFormatEnabled,
                                      boolean logstashEnabled) {
            this.fields = fields;
            this.jsonFormatEnabled = jsonFormatEnabled;
            this.logstashEnabled = logstashEnabled;
        }

        // ── Setters for logback-spring.xml injection ──

        /** Sets the JSON global custom fields string, e.g. {@code {"app":"svc","env":"prod"}}. */
        public void setFields(String fields) {
            this.fields = fields;
        }

        /** Enables or disables the structured JSON console appender (default: {@code true}). */
        public void setJsonFormatEnabled(boolean jsonFormatEnabled) {
            this.jsonFormatEnabled = jsonFormatEnabled;
        }

        /** Enables or disables the Logstash TCP appender (default: {@code false}). */
        public void setLogstashEnabled(boolean logstashEnabled) {
            this.logstashEnabled = logstashEnabled;
        }

        @Override
        public boolean isResetResistant() {
            return true;
        }

        @Override
        public void onStart(LoggerContext loggerContext) {
            configureAppenders(loggerContext);
        }

        @Override
        public void onReset(LoggerContext loggerContext) {
            configureAppenders(loggerContext);
        }

        @Override
        public void onStop(LoggerContext loggerContext) {
            // nothing to release
        }

        @Override
        public void onLevelChange(Logger logger, Level level) {
            // nothing to do
        }

        private void configureAppenders(LoggerContext loggerContext) {
            if (jsonFormatEnabled) {
                addJSONAppender(loggerContext, fields);
            }
            if (logstashEnabled) {
                addLogstashAppender(loggerContext, fields);
            }
        }
    }
}

