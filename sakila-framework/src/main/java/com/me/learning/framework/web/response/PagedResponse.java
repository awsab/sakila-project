package com.me.learning.framework.web.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard paginated response wrapper for list/search endpoints.
 *
 * <p>Wraps Spring Data's {@link Page} into a consistent JSON shape
 * that every eVisa consumer API can rely on.
 *
 * <pre>{@code
 * // In a controller:
 * Page<VisaApplication> page = service.findAll(pageable);
 * return ResponseEntity.ok(
 *     ApiResponse.ok(PagedResponse.from(page), "Applications retrieved"));
 * }</pre>
 *
 * <p>JSON output:
 * <pre>{@code
 * {
 *   "content": [...],
 *   "page": 0,
 *   "size": 20,
 *   "totalElements": 150,
 *   "totalPages": 8,
 *   "first": true,
 *   "last": false
 * }
 * }</pre>
 *
 * @param <T> type of items in the page
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponse<T> {

    private final List<T> content;
    private final int     page;
    private final int     size;
    private final long    totalElements;
    private final int     totalPages;
    private final boolean first;
    private final boolean last;

    /**
     * Constructs a {@link PagedResponse} from a Spring Data {@link Page}.
     */
    public static <T> PagedResponse<T> from(Page<T> springPage) {
        return PagedResponse.<T>builder()
                .content(springPage.getContent())
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .first(springPage.isFirst())
                .last(springPage.isLast())
                .build();
    }

    /**
     * Constructs a {@link PagedResponse} from a Spring Data {@link Page}
     * with content already mapped to a different type (e.g. DTO).
     */
    public static <T, S> PagedResponse<T> from(Page<S> springPage, List<T> mappedContent) {
        return PagedResponse.<T>builder()
                .content(mappedContent)
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .first(springPage.isFirst())
                .last(springPage.isLast())
                .build();
    }
}
