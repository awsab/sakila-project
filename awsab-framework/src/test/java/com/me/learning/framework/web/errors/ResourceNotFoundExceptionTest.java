package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ResourceNotFoundException")
class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("3-arg constructor sets message and getters")
    void threeArgConstructorSetsFieldsAndMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Actor", "id", 42);

        assertThat(ex.getMessage()).isEqualTo("Actor not found with id : '42'");
        assertThat(ex.getResourceName()).isEqualTo("Actor");
        assertThat(ex.getFieldName()).isEqualTo("id");
        assertThat(ex.getFieldValue()).isEqualTo(42);
    }

    @Test
    @DisplayName("1-arg message constructor sets message, nulls for structured fields")
    void oneArgConstructorSetsMessageAndNullFields() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Actor not found");

        assertThat(ex.getMessage()).isEqualTo("Actor not found");
        assertThat(ex.getResourceName()).isNull();
        assertThat(ex.getFieldName()).isNull();
        assertThat(ex.getFieldValue()).isNull();
    }

    @Test
    @DisplayName("message+cause constructor propagates cause")
    void messageAndCauseConstructorPropagatesCause() {
        Throwable cause = new RuntimeException("root cause");
        ResourceNotFoundException ex = new ResourceNotFoundException("not found", cause);

        assertThat(ex.getMessage()).isEqualTo("not found");
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.getResourceName()).isNull();
    }

    @Test
    @DisplayName("is a RuntimeException")
    void isRuntimeException() {
        assertThat(new ResourceNotFoundException("Actor", "id", 1))
                .isInstanceOf(RuntimeException.class);
    }
}
