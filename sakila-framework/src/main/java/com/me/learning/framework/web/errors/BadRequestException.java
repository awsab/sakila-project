package com.me.learning.framework.web.errors;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;


public class BadRequestException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    public static final String PROBLEM_BASE_URL = "https://awsab.me.learning.com/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");


    private final String entityName;

    private final String errorKey;

    public BadRequestException(String defaultMessage, String entityName, String errorKey) {
        this(DEFAULT_TYPE, defaultMessage, entityName, errorKey);
    }

    public BadRequestException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(
                HttpStatus.BAD_REQUEST,
                ProblemDetailWithReason.ProblemDetailWithReasonBuilder.instance()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withType(type)
                        .withTitle(defaultMessage)
                        .withProperty("message", "error." + errorKey)
                        .withProperty("params", entityName)
                        .build(),
                null
        );
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public ProblemDetailWithReason getProblemDetailWithCause() {
        return (ProblemDetailWithReason) this.getBody();
    }
}
