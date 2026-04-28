package com.me.learning.framework.web.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.util.UriComponentsBuilder;

@DisplayName("LinkHeaderUtil")
class LinkHeaderUtilTest {

    private final LinkHeaderUtil util = new LinkHeaderUtil();

    private UriComponentsBuilder baseUri() {
        return UriComponentsBuilder.fromUriString("http://localhost/api/actors");
    }

    @Test
    @DisplayName("middle page includes next, prev, last and first links")
    void middlePageIncludesAllLinks() {
        List<String> items = List.of("Actor1", "Actor2");
        Page<String> page = new PageImpl<>(items, PageRequest.of(2, 2), 10);

        String link = util.prepareLinkHeaders(baseUri(), page);

        assertThat(link).contains("rel=\"next\"");
        assertThat(link).contains("rel=\"prev\"");
        assertThat(link).contains("rel=\"last\"");
        assertThat(link).contains("rel=\"first\"");
    }

    @Test
    @DisplayName("first page has next, last and first links but no prev")
    void firstPageHasNoPreview() {
        List<String> items = List.of("Actor1", "Actor2");
        Page<String> page = new PageImpl<>(items, PageRequest.of(0, 2), 10);

        String link = util.prepareLinkHeaders(baseUri(), page);

        assertThat(link).contains("rel=\"next\"");
        assertThat(link).doesNotContain("rel=\"prev\"");
        assertThat(link).contains("rel=\"last\"");
        assertThat(link).contains("rel=\"first\"");
    }

    @Test
    @DisplayName("last page has prev, last and first links but no next")
    void lastPageHasNoNext() {
        List<String> items = List.of("Actor9", "Actor10");
        Page<String> page = new PageImpl<>(items, PageRequest.of(4, 2), 10);

        String link = util.prepareLinkHeaders(baseUri(), page);

        assertThat(link).doesNotContain("rel=\"next\"");
        assertThat(link).contains("rel=\"prev\"");
        assertThat(link).contains("rel=\"first\"");
    }

    @Test
    @DisplayName("link URLs contain page and size query parameters")
    void linkUrlsContainPageAndSizeParams() {
        List<String> items = List.of("Actor1");
        Page<String> page = new PageImpl<>(items, PageRequest.of(1, 5), 20);

        String link = util.prepareLinkHeaders(baseUri(), page);

        assertThat(link).contains("page=");
        assertThat(link).contains("size=");
    }

    @Test
    @DisplayName("commas in URIs are percent-encoded")
    void linkUrlsEncodeCommas() {
        UriComponentsBuilder uriWithComma =
                UriComponentsBuilder.fromUriString("http://localhost/api/actors?filter=a,b");
        List<String> items = List.of("Actor1");
        Page<String> page = new PageImpl<>(items, PageRequest.of(0, 1), 5);

        String link = util.prepareLinkHeaders(uriWithComma, page);

        assertThat(link).doesNotContain("filter=a,b");
    }
}
