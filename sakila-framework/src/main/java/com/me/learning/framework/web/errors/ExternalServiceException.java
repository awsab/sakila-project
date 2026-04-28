package com.me.learning.framework.web.errors;

import lombok.Getter;

/**
 * Thrown when a call to a downstream / external service fails.
 * Maps to HTTP 502 Bad Gateway.
 *
 * <pre>{@code
 * try {
 *     passportService.validate(passportNumber);
 * } catch (FeignException e) {
 *     throw new ExternalServiceException("passport-service", "Passport validation failed", e);
 * }
 * }</pre>
 */
@Getter
public class ExternalServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Logical name of the downstream service that failed. */
    private final String serviceName;

    public ExternalServiceException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }

    public ExternalServiceException(String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
    }
}
