package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BusinessRuleViolationException")
class BusinessRuleViolationExceptionTest {

    @Test
    @DisplayName("2-arg constructor sets errorCode and message")
    void twoArgConstructorSetsErrorCodeAndMessage() {
        BusinessRuleViolationException ex =
                new BusinessRuleViolationException("EVS_BIZ_001", "Visa has expired");

        assertThat(ex.getMessage()).isEqualTo("Visa has expired");
        assertThat(ex.getErrorCode()).isEqualTo("EVS_BIZ_001");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    @DisplayName("3-arg constructor propagates cause")
    void threeArgConstructorPropagatesCause() {
        Throwable cause = new IllegalStateException("state error");
        BusinessRuleViolationException ex =
                new BusinessRuleViolationException("EVS_BIZ_002", "Rule violated", cause);

        assertThat(ex.getMessage()).isEqualTo("Rule violated");
        assertThat(ex.getErrorCode()).isEqualTo("EVS_BIZ_002");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("is a RuntimeException")
    void isRuntimeException() {
        assertThat(new BusinessRuleViolationException("CODE", "msg"))
                .isInstanceOf(RuntimeException.class);
    }
}
