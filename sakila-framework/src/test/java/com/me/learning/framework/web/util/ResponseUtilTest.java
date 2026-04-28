package com.me.learning.framework.web.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@DisplayName("ResponseUtil")
class ResponseUtilTest {

    @Test
    @DisplayName("wrapOrNotFound returns 200 with body when optional is present")
    void wrapOrNotFoundPresentReturns200() {
        Optional<String> maybeResponse = Optional.of("actor");

        ResponseEntity<String> entity = ResponseUtil.wrapOrNotFound(maybeResponse);

        assertThat(entity.getStatusCode().value()).isEqualTo(200);
        assertThat(entity.getBody()).isEqualTo("actor");
    }

    @Test
    @DisplayName("wrapOrNotFound throws ResponseStatusException when optional is empty")
    void wrapOrNotFoundEmptyThrowsException() {
        Optional<String> empty = Optional.empty();

        assertThatThrownBy(() -> ResponseUtil.wrapOrNotFound(empty))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    @DisplayName("wrapOrNotFound with headers returns headers in response")
    void wrapOrNotFoundWithHeadersIncludesHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Custom", "value");
        Optional<String> maybeResponse = Optional.of("film");

        ResponseEntity<String> entity = ResponseUtil.wrapOrNotFound(maybeResponse, headers);

        assertThat(entity.getStatusCode().value()).isEqualTo(200);
        assertThat(entity.getBody()).isEqualTo("film");
        assertThat(entity.getHeaders().getFirst("X-Custom")).isEqualTo("value");
    }

    @Test
    @DisplayName("wrapOrNotFound with null headers still returns body")
    void wrapOrNotFoundWithNullHeadersReturnsBody() {
        Optional<String> maybeResponse = Optional.of("language");

        ResponseEntity<String> entity = ResponseUtil.wrapOrNotFound(maybeResponse, null);

        assertThat(entity.getBody()).isEqualTo("language");
    }

    @Test
    @DisplayName("wrapOrNotFound with headers and empty optional throws 404")
    void wrapOrNotFoundWithHeadersAndEmptyThrows404() {
        HttpHeaders headers = new HttpHeaders();
        Optional<String> empty = Optional.empty();

        assertThatThrownBy(() -> ResponseUtil.wrapOrNotFound(empty, headers))
                .isInstanceOf(ResponseStatusException.class);
    }
}
