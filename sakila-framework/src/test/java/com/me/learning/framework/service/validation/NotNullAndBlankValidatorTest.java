package com.me.learning.framework.service.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("NotNullAndBlankValidator")
class NotNullAndBlankValidatorTest {


    private NotNullAndBlankValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new NotNullAndBlankValidator();
        // initialize() is a no-op but must be called for coverage
        validator.initialize(mock(NotNullAndBlank.class));
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    @DisplayName("null value returns false")
    void nullValueReturnsFalse() {
        assertThat(validator.isValid(null, context)).isFalse();
    }

    @Test
    @DisplayName("empty string returns false")
    void emptyStringReturnsFalse() {
        assertThat(validator.isValid("", context)).isFalse();
    }

    @Test
    @DisplayName("whitespace-only string returns false")
    void whitespaceOnlyReturnsFalse() {
        assertThat(validator.isValid("   ", context)).isFalse();
    }

    @Test
    @DisplayName("tab-only string returns false")
    void tabOnlyReturnsFalse() {
        assertThat(validator.isValid("\t\n", context)).isFalse();
    }

    @Test
    @DisplayName("valid non-blank string returns true")
    void validStringReturnsTrue() {
        assertThat(validator.isValid("John", context)).isTrue();
    }

    @Test
    @DisplayName("string with surrounding whitespace but non-blank returns true")
    void stringWithSurroundingWhitespaceReturnsTrue() {
        assertThat(validator.isValid("  hello  ", context)).isTrue();
    }
}
