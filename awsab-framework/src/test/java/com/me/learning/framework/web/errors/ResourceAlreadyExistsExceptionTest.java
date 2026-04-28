package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ResourceAlreadyExistsException")
class ResourceAlreadyExistsExceptionTest {

    @Test
    @DisplayName("message constructor sets message")
    void messageConstructorSetsMessage() {
        ResourceAlreadyExistsException ex =
                new ResourceAlreadyExistsException("Actor already exists");

        assertThat(ex.getMessage()).isEqualTo("Actor already exists");
    }

    @Test
    @DisplayName("3-arg constructor formats message")
    void threeArgConstructorFormatsMessage() {
        ResourceAlreadyExistsException ex =
                new ResourceAlreadyExistsException("Language", "name", "English");

        assertThat(ex.getMessage()).isEqualTo("Language already exists with name: English");
    }

    @Test
    @DisplayName("is a RuntimeException")
    void isRuntimeException() {
        assertThat(new ResourceAlreadyExistsException("msg"))
                .isInstanceOf(RuntimeException.class);
    }
}
