package com.me.learning.framework.service.filter;

import java.util.Objects;

/**
 * Filter class for {@link Comparable} type attributes.
 *
 * @param <FIELD_T> a FIELD_T object.
 */

public class RangeFilter<FIELD_T extends Comparable<? super FIELD_T>> extends BaseFilter<FIELD_T> {

    private static final long serialVersionUID = 1L;
    private FIELD_T greaterThan;
    private FIELD_T lessThan;
    private FIELD_T greaterThanOrEqual;
    private FIELD_T lessThanOrEqual;


    /**
     * <p>Constructor for RWithRangeFilter.</p>
     */
    public RangeFilter() {
    }

    /**
     * <p>Constructor for RWithRangeFilter.</p>
     *
     * @param filter a {@link RangeFilter} object.
     */
    public RangeFilter(RangeFilter<FIELD_T> filter) {
        super(filter);
        this.greaterThan = filter.greaterThan;
        this.lessThan = filter.lessThan;
        this.greaterThanOrEqual = filter.greaterThanOrEqual;
        this.lessThanOrEqual = filter.lessThanOrEqual;
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link RangeFilter} object.
     */
    @Override
    public RangeFilter<FIELD_T> copy() {
        return new RangeFilter<>(this);
    }

    /**
     * <p>Getter for the field <code>greaterThan</code>.</p>
     *
     * @return a FIELD_T object.
     */
    public FIELD_T getGreaterThan() {
        return greaterThan;
    }

    /**
     * Setter for the field <code>greaterThan</code>.
     *
     * @param greaterThan a {@link FIELD_T} object.
     * @return a {@link RangeFilter} object.
     */
    public RangeFilter<FIELD_T> setGreaterThan(FIELD_T greaterThan) {
        this.greaterThan = greaterThan;
        return this;
    }

    /**
     * <p>Getter for the field <code>lessThan</code>.</p>
     *
     * @return a FIELD_T object.
     */
    public FIELD_T getLessThan() {
        return lessThan;
    }

    /**
     * Setter for the field <code>lessThan</code>.
     *
     * @param lessThan a {@link FIELD_T} object.
     * @return a {@link RangeFilter} object.
     */
    public RangeFilter<FIELD_T> setLessThan(FIELD_T lessThan) {
        this.lessThan = lessThan;
        return this;
    }

    /**
     * <p>Getter for the field <code>greaterThanOrEqual</code>.</p>
     *
     * @return a FIELD_T object.
     */
    public FIELD_T getGreaterThanOrEqual() {
        return greaterThanOrEqual;
    }


    /**
     * Setter for the field <code>greaterThanOrEqual</code>.
     *
     * @param greaterThanOrEqual a {@link FIELD_T} object.
     * @return a {@link RangeFilter} object.
     */
    public RangeFilter<FIELD_T> setGreaterThanOrEqual(FIELD_T greaterThanOrEqual) {
        this.greaterThanOrEqual = greaterThanOrEqual;
        return this;
    }

    /**
     * <p>Getter for the field <code>lessThanOrEqual</code>.</p>
     *
     * @return a FIELD_T object.
     */
    public FIELD_T getLessThanOrEqual() {
        return lessThanOrEqual;
    }

    /**
     * Setter for the field <code>lessThanOrEqual</code>.
     * @param lessThanOrEqual a {@link FIELD_T} object.
     * @return a {@link RangeFilter} object.
     */
    public RangeFilter<FIELD_T> setLessThanOrEqual(FIELD_T lessThanOrEqual) {
        this.lessThanOrEqual = lessThanOrEqual;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        RangeFilter<?> that = (RangeFilter<?>) o;
        return Objects.equals(greaterThan, that.greaterThan) &&
                Objects.equals(lessThan, that.lessThan) &&
                Objects.equals(greaterThanOrEqual, that.greaterThanOrEqual) &&
                Objects.equals(lessThanOrEqual, that.lessThanOrEqual);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), greaterThan, lessThan, greaterThanOrEqual, lessThanOrEqual);
    }

    @Override
    public String toString() {
        return getFilterName() +
                " [" +
                (getEquals() != null ? "equals=" + getEquals() + ", " : "") +
                (getNotEquals() != null ? "notEquals=" + getNotEquals() + ", " : "") +
                (getSpecified() != null ? "specified=" + getSpecified() + ", " : "") +
                (getIn() != null ? "in=" + getIn() + ", " : "") +
                (getNotIn() != null ? "notIn=" + getNotIn() + ", " : "") +
                (getGreaterThan() != null ? "greaterThan=" + getGreaterThan() + ", " : "") +
                (getLessThan() != null ? "lessThan=" + getLessThan() + ", " : "") +
                (getGreaterThanOrEqual() != null ? "greaterThanOrEqual=" + getGreaterThanOrEqual() + ", " : "") +
                (getLessThanOrEqual() != null ? "lessThanOrEqual=" + getLessThanOrEqual() : "") +
                "]";
    }

}
