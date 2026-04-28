package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DuplicateResourceException")
class DuplicateResourceExceptionTest {

    @Test
    @DisplayName("3-arg constructor sets message and getters")
    void threeArgConstructorSetsFieldsAndMessage() {
        DuplicateResourceException ex = new DuplicateResourceException("Film", "title", "Inception");

        assertThat(ex.getMessage()).isEqualTo("Film already exists with title : 'Inception'");
        assertThat(ex.getResourceName()).isEqualTo("Film");
        assertThat(ex.getFieldName()).isEqualTo("title");
        assertThat(ex.getFieldValue()).isEqualTo("Inception");
    }

    @Test
    @DisplayName("1-arg message constructor sets message, nulls for structured fields")
    void oneArgConstructorSetsMessageAndNullFields() {
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate entry");

        assertThat(ex.getMessage()).isEqualTo("Duplicate entry");
        assertThat(ex.getResourceName()).isNull();
        assertThat(ex.getFieldName()).isNull();
        assertThat(ex.getFieldValue()).isNull();
    }

    @Test
    @DisplayName("message+cause constructor propagates cause")
    void messageAndCauseConstructorPropagatesCause() {
        Throwable cause = new RuntimeException("constraint violation");
        DuplicateResourceException ex = new DuplicateResourceException("already exists", cause);

        assertThat(ex.getMessage()).isEqualTo("already exists");
        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.getResourceName()).isNull();
    }
}
