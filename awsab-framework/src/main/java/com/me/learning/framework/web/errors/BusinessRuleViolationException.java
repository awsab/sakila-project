package com.me.learning.framework.web.errors;

import lombok.Getter;

/**
 * Thrown when a domain business rule is violated.
 * Maps to HTTP 422 Unprocessable Entity.
 *
 * <pre>{@code
 * if (application.isExpired()) {
 *     throw new BusinessRuleViolationException(
 *         "EVS_BIZ_001", "Visa application has expired and cannot be resubmitted");
 * }
 * }</pre>
 */
@Getter
public class BusinessRuleViolationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** eVisa-specific error code for this rule violation. */
    private final String errorCode;

    public BusinessRuleViolationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessRuleViolationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
