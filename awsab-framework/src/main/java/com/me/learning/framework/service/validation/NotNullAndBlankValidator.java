/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 04/05/2025
 * Usage    : Validator implementation for NotNullAndBlank annotation
 * Since    : Version 1.0
 */
package com.me.learning.framework.service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the NotNullAndBlank annotation.
 * Validates that a String value is both not null and not blank (not empty and not just whitespace).
 */
public class NotNullAndBlankValidator implements ConstraintValidator<NotNullAndBlank, String> {

    @Override
    public void initialize(NotNullAndBlank constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Check if the value is not null and not blank
        return value != null && !value.isBlank();
    }
}

