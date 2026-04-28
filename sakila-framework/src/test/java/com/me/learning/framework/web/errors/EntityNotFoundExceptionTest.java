package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("EntityNotFoundException")
class EntityNotFoundExceptionTest {

    @Test
    @DisplayName("3-arg constructor sets entityName and errorKey")
    void threeArgConstructorSetsFields() {
        EntityNotFoundException ex =
                new EntityNotFoundException("Actor not found", "actor", "actor.notfound");

        assertThat(ex.getEntityName()).isEqualTo("actor");
        assertThat(ex.getErrorKey()).isEqualTo("actor.notfound");
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("4-arg constructor uses provided URI")
    void fourArgConstructorUsesProvidedUri() {
        URI customUri = URI.create("https://example.com/problem/not-found");
        EntityNotFoundException ex =
                new EntityNotFoundException(customUri, "Not found", "film", "film.missing");

        assertThat(ex.getEntityName()).isEqualTo("film");
        assertThat(ex.getErrorKey()).isEqualTo("film.missing");
    }

    @Test
    @DisplayName("getProblemDetailWithCause returns ProblemDetailWithReason")
    void getProblemDetailWithCauseReturnsProblemDetail() {
        EntityNotFoundException ex =
                new EntityNotFoundException("msg", "entity", "key");

        assertThat(ex.getProblemDetailWithCause()).isNotNull();
        assertThat(ex.getProblemDetailWithCause()).isInstanceOf(ProblemDetailWithReason.class);
    }
}
