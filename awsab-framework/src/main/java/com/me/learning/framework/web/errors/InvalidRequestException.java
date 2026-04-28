/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 07/04/2026
 * Usage    : Exception thrown when a request is invalid or malformed
 * Since    : Version 1.0
 */
package com.me.learning.framework.web.errors;

import java.io.Serial;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


/**
 * Exception thrown when a request is invalid or contains malformed data.
 * This is a runtime exception that typically results in an HTTP 400 Bad Request response.
 */
public class InvalidRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new InvalidRequestException with the specified message.
     *
     * @param message the detail message explaining why the request is invalid
     */
    public InvalidRequestException(@NonNull String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidRequestException with the specified message and cause.
     *
     * @param message the detail message explaining why the request is invalid
     * @param cause the cause of the exception
     */
    public InvalidRequestException(@NonNull String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

