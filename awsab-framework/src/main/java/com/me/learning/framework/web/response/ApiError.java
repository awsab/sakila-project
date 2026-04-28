package com.me.learning.framework.web.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Structured error detail embedded inside {@link ApiResponse} on failure.
 *
 * <p>Error codes follow the convention:
 * {@code EVS_{HTTP_STATUS}_{SEQUENCE}} — e.g. {@code EVS_404_001}.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    /** eVisa error code — unique identifier for this error type. */
    private final String code;

    /** Technical detail message (safe to expose in non-PROD environments). */
    private final String detail;

    /**
     * Field-level validation errors.
     * Present only for 400 Bad Request responses.
     */
    private final List<FieldError> fieldErrors;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {
        /** The request field that failed validation. */
        private final String field;
        /** The rejected value (may be null). */
        private final Object rejectedValue;
        /** Human-readable reason for rejection. */
        private final String message;
    }
}
