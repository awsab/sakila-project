/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 07/04/2026
 * Usage    : Exception thrown when a requested resource is not found
 * Since    : Version 1.0
 */
package com.me.learning.framework.web.errors;

import java.io.Serial;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


/**
 * Exception thrown when a requested resource cannot be found in the system.
 * This is a runtime exception that typically results in an HTTP 404 response.
 */
public class ResourceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Constructs a new ResourceNotFoundException with the specified resource details.
     *
     * @param resourceName the name of the resource that was not found
     * @param fieldName the name of the field used to search for the resource
     * @param fieldValue the value of the field used to search for the resource
     */
    public ResourceNotFoundException(@NonNull String resourceName, @NonNull String fieldName, @NonNull Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructs a new ResourceNotFoundException with a custom message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(@NonNull String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    /**
     * Constructs a new ResourceNotFoundException with a custom message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ResourceNotFoundException(@NonNull String message, @Nullable Throwable cause) {
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

