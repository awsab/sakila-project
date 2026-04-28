package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ServiceException")
class ServiceExceptionTest {

    @Test
    @DisplayName("message constructor sets message")
    void messageConstructorSetsMessage() {
        ServiceException ex = new ServiceException("Service failed");

        assertThat(ex.getMessage()).isEqualTo("Service failed");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    @DisplayName("message+cause constructor propagates cause")
    void messageAndCauseConstructorPropagatesCause() {
        Throwable cause = new RuntimeException("root");
        ServiceException ex = new ServiceException("Service error", cause);

        assertThat(ex.getMessage()).isEqualTo("Service error");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("cause-only constructor wraps cause")
    void causeOnlyConstructorWrapsCause() {
        Throwable cause = new NullPointerException("npe");
        ServiceException ex = new ServiceException(cause);

        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("is a RuntimeException")
    void isRuntimeException() {
        assertThat(new ServiceException("msg")).isInstanceOf(RuntimeException.class);
    }
}
