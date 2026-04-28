/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 07/04/2026
 * Usage    : Exception thrown when attempting to create a resource that already exists
 * Since    : Version 1.0
 */
package com.me.learning.framework.web.errors;

import java.io.Serial;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


/**
 * Exception thrown when attempting to create a resource that already exists in the system.
 * This is a runtime exception that typically results in an HTTP 409 Conflict response.
 */
public class DuplicateResourceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Constructs a new DuplicateResourceException with the specified resource details.
     *
     * @param resourceName the name of the resource that already exists
     * @param fieldName the name of the field that has a duplicate value
     * @param fieldValue the duplicate value
     */
    public DuplicateResourceException(@NonNull String resourceName, @NonNull String fieldName, @NonNull Object fieldValue) {
        super(String.format("%s already exists with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructs a new DuplicateResourceException with a custom message.
     *
     * @param message the detail message
     */
    public DuplicateResourceException(@NonNull String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    /**
     * Constructs a new DuplicateResourceException with a custom message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DuplicateResourceException(@NonNull String message, @Nullable Throwable cause) {
        super(message, cause);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    public @Nullable String getResourceName() {
        return resourceName;
    }

    public @Nullable String getFieldName() {
        return fieldName;
    }

    public @Nullable Object getFieldValue() {
        return fieldValue;
    }
}

