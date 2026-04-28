/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 07/04/2026
 * Usage    : Exception thrown when a data integrity constraint is violated
 * Since    : Version 1.0
 */
package com.me.learning.framework.web.errors;

import java.io.Serial;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


/**
 * Exception thrown when a database operation violates data integrity constraints.
 * This is a runtime exception that typically results in an HTTP 409 Conflict response.
 */
public class DataIntegrityViolationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new DataIntegrityViolationException with the specified message.
     *
     * @param message the detail message explaining the integrity violation
     */
    public DataIntegrityViolationException(@NonNull String message) {
        super(message);
    }

    /**
     * Constructs a new DataIntegrityViolationException with the specified message and cause.
     *
     * @param message the detail message explaining the integrity violation
     * @param cause the cause of the exception
     */
    public DataIntegrityViolationException(@NonNull String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

