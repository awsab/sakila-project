package com.me.learning.framework.service.filter;

/**
 * Filter class for {@link Long} type attributes.
 *
 * @see RangeFilter
 */

public class LongFilter extends RangeFilter<Long> {

    private static final long serialVersionUID = 1L;


    /**
     * <p>Constructor for RLongFilter.</p>
     */
    public LongFilter() {
    }

    /**
     * <p>Constructor for RRLongFilter.</p>
     *
     * @param filter a {@link LongFilter} object.
     */
    public LongFilter(LongFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link LongFilter} object.
     */
    @Override
    public LongFilter copy() {
        return new LongFilter(this);
    }
}
