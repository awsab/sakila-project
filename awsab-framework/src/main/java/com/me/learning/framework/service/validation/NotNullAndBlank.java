/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 04/05/2025
 * Usage    : Custom validation annotation that combines @NotNull and @NotBlank validations
 * Since    : Version 1.0
 */
package com.me.learning.framework.service.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

/**
 * Validation annotation that ensures a String value is both not null and not blank.
 * This combines the functionality of @NotNull and @NotBlank in a single annotation.
 */
@Documented
@Constraint (validatedBy = NotNullAndBlankValidator.class)
@Target ({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention (RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface NotNullAndBlank {

    String message () default "Value cannot be null or blank";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};
}
