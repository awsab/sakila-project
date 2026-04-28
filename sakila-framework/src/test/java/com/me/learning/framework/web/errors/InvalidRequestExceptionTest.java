package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InvalidRequestException")
class InvalidRequestExceptionTest {

    @Test
    @DisplayName("message constructor sets message")
    void messageConstructorSetsMessage() {
        InvalidRequestException ex = new InvalidRequestException("Invalid payload");

        assertThat(ex.getMessage()).isEqualTo("Invalid payload");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    @DisplayName("message+cause constructor propagates cause")
    void messageAndCauseConstructorPropagatesCause() {
        Throwable cause = new IllegalArgumentException("bad arg");
        InvalidRequestException ex = new InvalidRequestException("Request invalid", cause);

        assertThat(ex.getMessage()).isEqualTo("Request invalid");
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
