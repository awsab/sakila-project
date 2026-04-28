package com.me.learning.framework.service.filter;

import java.util.UUID;

/**
 * Filter class for {@link UUID} type attributes.
 *
 * @see BaseFilter
 */
public class GUIDFilter extends BaseFilter<UUID> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for RUUIDFilter.</p>
     */
    public GUIDFilter() {
    }


    /**
     * <p>Constructor for RUUIDFilter.</p>
     * @param filter a {@link GUIDFilter} object.
     */
    public GUIDFilter(GUIDFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     * @return a {@link GUIDFilter} object.
     */
    @Override
    public GUIDFilter copy() {
        return new GUIDFilter(this);
    }
}
