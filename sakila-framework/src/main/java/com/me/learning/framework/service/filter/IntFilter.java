package com.me.learning.framework.service.filter;


/**
 * Filter class for {@link Integer} type attributes.
 *
 * @see RangeFilter
 */
public class IntFilter extends RangeFilter<Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for RIntegerFilter.</p>
     */
    public IntFilter() {
    }

    /**
     * <p>Constructor for RIntegerFilter.</p>
     *
     * @param filter a {@link IntFilter} object.
     */
    public IntFilter(IntFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     * @return a {@link IntFilter} object.
     */
    @Override
    public IntFilter copy() {
        return new IntFilter(this);
    }
}
