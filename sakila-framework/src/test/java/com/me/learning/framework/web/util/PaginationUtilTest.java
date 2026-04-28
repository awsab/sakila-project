package com.me.learning.framework.web.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

@DisplayName("PaginationUtil")
class PaginationUtilTest {

    @Test
    @DisplayName("generatePaginationHttpHeaders includes X-Total-Count header")
    void generateHeadersIncludesXTotalCount() {
        List<String> items = List.of("A", "B", "C");
        Page<String> page = new PageImpl<>(items, PageRequest.of(0, 3), 15);
        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromUriString("http://localhost/api/actors");

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder, page);

        assertThat(headers.getFirst("X-Total-Count")).isEqualTo("15");
    }

    @Test
    @DisplayName("generatePaginationHttpHeaders includes Link header")
    void generateHeadersIncludesLinkHeader() {
        List<String> items = List.of("A", "B");
        Page<String> page = new PageImpl<>(items, PageRequest.of(1, 2), 10);
        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromUriString("http://localhost/api/films");

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder, page);

        assertThat(headers.getFirst(HttpHeaders.LINK)).isNotBlank();
        assertThat(headers.getFirst(HttpHeaders.LINK)).contains("rel=\"next\"");
        assertThat(headers.getFirst(HttpHeaders.LINK)).contains("rel=\"prev\"");
    }
}
