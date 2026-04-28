package com.me.learning.framework.service.filter;



public class BoolFilter extends BaseFilter<Boolean> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for RBooleanFilter.</p>
     */
    public BoolFilter() {
    }

    /**
     * <p>Constructor for RBooleanFilter.</p>
     *
     * @param filter a {@link BoolFilter} object.
     */
    public BoolFilter(BoolFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link BoolFilter} object.
     */
    @Override
    public BoolFilter copy() {
        return new BoolFilter(this);
    }
}
