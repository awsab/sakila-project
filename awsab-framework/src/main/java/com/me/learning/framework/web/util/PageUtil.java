package com.me.learning.framework.web.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Utility Class for creating Pageable Object
 */
public interface PageUtil {

    /**
     *
     * @param list {@link List} if objects
     * @param pageable, which has pagination data
     * @param <T> Generic Type Object
     *
     * @return page with appropriate start and end value
     * @throws {@link IllegalArgumentException}
     */
    static <T> Page<T> createPageableFromList(List<T> list, Pageable pageable) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Pageable List is empty");
        }

        int startPage = pageable.getPageNumber() * pageable.getPageSize();
        if (startPage > list.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        int endPage = Math.min(startPage + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(startPage, endPage), pageable, list.size());
    }
}
