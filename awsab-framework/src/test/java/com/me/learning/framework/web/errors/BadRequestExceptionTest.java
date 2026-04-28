package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("BadRequestException")
class BadRequestExceptionTest {

    @Test
    @DisplayName("3-arg constructor sets entityName, errorKey and uses default type")
    void threeArgConstructorUsesDefaultType() {
        BadRequestException ex = new BadRequestException("Invalid data", "actor", "actor.invalid");

        assertThat(ex.getEntityName()).isEqualTo("actor");
        assertThat(ex.getErrorKey()).isEqualTo("actor.invalid");
        assertThat(ex.getMessage()).contains("Invalid data");
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("4-arg constructor uses custom URI type")
    void fourArgConstructorUsesCustomType() {
        URI customUri = URI.create("https://example.com/problem/custom");
        BadRequestException ex =
                new BadRequestException(customUri, "Custom error", "film", "film.bad");

        assertThat(ex.getEntityName()).isEqualTo("film");
        assertThat(ex.getErrorKey()).isEqualTo("film.bad");
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("getProblemDetailWithCause returns ProblemDetailWithReason")
    void getProblemDetailWithCauseReturnsProblemDetail() {
        BadRequestException ex = new BadRequestException("msg", "entity", "key");

        assertThat(ex.getProblemDetailWithCause()).isNotNull();
        assertThat(ex.getProblemDetailWithCause()).isInstanceOf(ProblemDetailWithReason.class);
    }

    @Test
    @DisplayName("PROBLEM_BASE_URL and DEFAULT_TYPE constants are set")
    void constantsAreSet() {
        assertThat(BadRequestException.PROBLEM_BASE_URL)
                .isEqualTo("https://awsab.me.learning.com/problem");
        assertThat(BadRequestException.DEFAULT_TYPE)
                .isEqualTo(URI.create("https://awsab.me.learning.com/problem/problem-with-message"));
    }
}
