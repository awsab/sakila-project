/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 15/04/2026
 * Usage    : Structured logging configuration properties for sakila-framework
 * Since    : Version 1.0
 */
package com.me.learning.framework.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed configuration properties for Sakila structured logging.
 *
 * <p>All properties are bound under the {@code sakila.logging} prefix and can be
 * set in any consuming service's {@code application.yaml}:
 *
 * <pre>{@code
 * sakila:
 *   logging:
 *     json-format-enabled: true      # default: true
 *     logstash-enabled: false        # default: false
 *     logstash-host: logstash-svc    # default: localhost
 *     logstash-port: 5000            # default: 5000
 * }</pre>
 */
@ConfigurationProperties(prefix = "sakila.logging")
public class SakilaLoggingProperties {

    /** Enable structured JSON console output (Loki/Grafana compatible). Default: {@code true}. */
    private boolean jsonFormatEnabled = true;

    /** Enable Logstash TCP appender. Default: {@code false}. */
    private boolean logstashEnabled;

    /** Logstash host. Default: {@code localhost}. */
    private String logstashHost = "localhost";

    /** Logstash TCP port. Default: {@code 5000}. */
    private int logstashPort = 5000;

    public boolean isJsonFormatEnabled() {
        return jsonFormatEnabled;
    }

    public void setJsonFormatEnabled(boolean jsonFormatEnabled) {
        this.jsonFormatEnabled = jsonFormatEnabled;
    }

    public boolean isLogstashEnabled() {
        return logstashEnabled;
    }

    public void setLogstashEnabled(boolean logstashEnabled) {
        this.logstashEnabled = logstashEnabled;
    }

    public String getLogstashHost() {
        return logstashHost;
    }

    public void setLogstashHost(String logstashHost) {
        this.logstashHost = logstashHost;
    }

    public int getLogstashPort() {
        return logstashPort;
    }

    public void setLogstashPort(int logstashPort) {
        this.logstashPort = logstashPort;
    }
}

