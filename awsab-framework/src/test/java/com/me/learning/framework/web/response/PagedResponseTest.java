package com.me.learning.framework.web.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@DisplayName("PagedResponse")
class PagedResponseTest {

    @Test
    @DisplayName("from(Page) maps all pagination fields correctly")
    void fromPageMapsAllFields() {
        List<String> items = List.of("Actor1", "Actor2", "Actor3");
        Page<String> page = new PageImpl<>(items, PageRequest.of(0, 3), 10);

        PagedResponse<String> response = PagedResponse.from(page);

        assertThat(response.getContent()).containsExactly("Actor1", "Actor2", "Actor3");
        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(3);
        assertThat(response.getTotalElements()).isEqualTo(10);
        assertThat(response.getTotalPages()).isEqualTo(4);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isFalse();
    }

    @Test
    @DisplayName("from(Page) marks last page correctly")
    void fromPageLastPage() {
        List<String> items = List.of("Actor9", "Actor10");
        Page<String> page = new PageImpl<>(items, PageRequest.of(3, 3), 11);

        PagedResponse<String> response = PagedResponse.from(page);

        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isTrue();
        assertThat(response.getPage()).isEqualTo(3);
    }

    @Test
    @DisplayName("from(Page, List) maps page metadata with mapped content")
    void fromPageWithMappedContentMapsCorrectly() {
        List<Integer> sourceItems = List.of(1, 2, 3);
        List<String> mappedItems = List.of("One", "Two", "Three");
        Page<Integer> page = new PageImpl<>(sourceItems, PageRequest.of(1, 3), 9);

        PagedResponse<String> response = PagedResponse.from(page, mappedItems);

        assertThat(response.getContent()).containsExactly("One", "Two", "Three");
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getSize()).isEqualTo(3);
        assertThat(response.getTotalElements()).isEqualTo(9);
        assertThat(response.getTotalPages()).isEqualTo(3);
    }
}
