package com.me.learning.framework.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SakilaLoggingProperties")
class SakilaLoggingPropertiesTest {

    @Test
    @DisplayName("default values are correct")
    void defaultValuesAreCorrect() {
        SakilaLoggingProperties props = new SakilaLoggingProperties();

        assertThat(props.isJsonFormatEnabled()).isTrue();
        assertThat(props.isLogstashEnabled()).isFalse();
        assertThat(props.getLogstashHost()).isEqualTo("localhost");
        assertThat(props.getLogstashPort()).isEqualTo(5000);
    }

    @Test
    @DisplayName("setJsonFormatEnabled toggles the flag")
    void setJsonFormatEnabledTogglesFlag() {
        SakilaLoggingProperties props = new SakilaLoggingProperties();
        props.setJsonFormatEnabled(false);

        assertThat(props.isJsonFormatEnabled()).isFalse();
    }

    @Test
    @DisplayName("setLogstashEnabled toggles the flag")
    void setLogstashEnabledTogglesFlag() {
        SakilaLoggingProperties props = new SakilaLoggingProperties();
        props.setLogstashEnabled(true);

        assertThat(props.isLogstashEnabled()).isTrue();
    }

    @Test
    @DisplayName("setLogstashHost updates the host")
    void setLogstashHostUpdatesHost() {
        SakilaLoggingProperties props = new SakilaLoggingProperties();
        props.setLogstashHost("logstash-prod.internal");

        assertThat(props.getLogstashHost()).isEqualTo("logstash-prod.internal");
    }

    @Test
    @DisplayName("setLogstashPort updates the port")
    void setLogstashPortUpdatesPort() {
        SakilaLoggingProperties props = new SakilaLoggingProperties();
        props.setLogstashPort(5044);

        assertThat(props.getLogstashPort()).isEqualTo(5044);
    }
}

