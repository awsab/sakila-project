package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DataIntegrityViolationException")
class DataIntegrityViolationExceptionTest {

    @Test
    @DisplayName("message constructor sets message")
    void messageConstructorSetsMessage() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("Unique constraint violated");

        assertThat(ex.getMessage()).isEqualTo("Unique constraint violated");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    @DisplayName("message+cause constructor propagates cause")
    void messageAndCauseConstructorPropagatesCause() {
        Throwable cause = new RuntimeException("DB error");
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("Constraint error", cause);

        assertThat(ex.getMessage()).isEqualTo("Constraint error");
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
