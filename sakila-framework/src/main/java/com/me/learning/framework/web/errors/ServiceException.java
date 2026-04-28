/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 07/04/2026
 * Usage    : Generic service layer exception for unexpected errors
 * Since    : Version 1.0
 */
package com.me.learning.framework.web.errors;

import java.io.Serial;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


/**
 * Generic service layer exception for unexpected errors that occur during business logic execution.
 * This is a runtime exception that typically results in an HTTP 500 Internal Server Error response.
 */
public class ServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ServiceException with the specified message.
     *
     * @param message the detail message explaining the error
     */
    public ServiceException(@NonNull String message) {
        super(message);
    }

    /**
     * Constructs a new ServiceException with the specified message and cause.
     *
     * @param message the detail message explaining the error
     * @param cause the cause of the exception
     */
    public ServiceException(@NonNull String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ServiceException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public ServiceException(@Nullable Throwable cause) {
        super(cause);
    }
}

