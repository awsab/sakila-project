package com.me.learning.framework.web.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("PageUtil")
class PageUtilTest {

    @Test
    @DisplayName("first page returns correct sublist")
    void firstPageReturnsCorrectSublist() {
        List<String> all = List.of("A", "B", "C", "D", "E");
        Pageable pageable = PageRequest.of(0, 2);

        Page<String> page = PageUtil.createPageableFromList(all, pageable);

        assertThat(page.getContent()).containsExactly("A", "B");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.isFirst()).isTrue();
    }

    @Test
    @DisplayName("second page returns correct sublist")
    void secondPageReturnsCorrectSublist() {
        List<String> all = List.of("A", "B", "C", "D", "E");
        Pageable pageable = PageRequest.of(1, 2);

        Page<String> page = PageUtil.createPageableFromList(all, pageable);

        assertThat(page.getContent()).containsExactly("C", "D");
    }

    @Test
    @DisplayName("last page with partial content returns remainder")
    void lastPageReturnsRemainder() {
        List<String> all = List.of("A", "B", "C", "D", "E");
        Pageable pageable = PageRequest.of(2, 2);

        Page<String> page = PageUtil.createPageableFromList(all, pageable);

        assertThat(page.getContent()).containsExactly("E");
        assertThat(page.isLast()).isTrue();
    }

    @Test
    @DisplayName("page number beyond list size returns empty page")
    void pageNumberBeyondListReturnsEmptyPage() {
        List<String> all = List.of("A", "B");
        Pageable pageable = PageRequest.of(5, 2);

        Page<String> page = PageUtil.createPageableFromList(all, pageable);

        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("empty list throws IllegalArgumentException")
    void emptyListThrowsIllegalArgumentException() {
        List<String> empty = List.of();
        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> PageUtil.createPageableFromList(empty, pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pageable List is empty");
    }
}
