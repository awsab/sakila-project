package com.me.learning.framework.service.filter;


/**
 * Filter class for {@link Short} type attributes.
 *
 * @see RangeFilter
 */
public class ShortFilter extends RangeFilter<Short> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for RShortFilter.</p>
     */
    public ShortFilter() {
    }

    /**
     * <p>Constructor for RShortFilter.</p>
     * @param filter a {@link ShortFilter} object.
     */
    public ShortFilter(ShortFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     * @return a {@link ShortFilter} object.
     */
    @Override
    public ShortFilter copy() {
        return new ShortFilter(this);
    }
}
