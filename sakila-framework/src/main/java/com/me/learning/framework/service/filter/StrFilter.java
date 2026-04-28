package com.me.learning.framework.service.filter;

import java.util.Objects;

/**
 * Filter class for {@link String} type attributes.
 */

public class StrFilter extends BaseFilter<String> {

    private static final long serialVersionUID = 1L;

    private String contains;
    private String doesNotContain;

    /**
     * <p>Constructor for RStringFilter.</p>
     */
    public StrFilter() {
    }

    /**
     * <p>Constructor for RStringFilter.</p>
     *
     * @param filter a {@link StrFilter} object.
     */
    public StrFilter(StrFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link StrFilter} object.
     */
    @Override
    public StrFilter copy() {
        return new StrFilter(this);
    }

    /**
     * <p>Getter for the field <code>contains</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getContains() {
        return contains;
    }

    /**
     * Setter for the field <code>contains</code>.
     *
     * @param contains a {@link String} object.
     * @return a {@link StrFilter} object.
     */
    public StrFilter setContains(String contains) {
        this.contains = contains;
        return this;
    }

    /**
     * <p>Getter for the field <code>doesNotContain</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getDoesNotContain() {
        return doesNotContain;
    }

    /**
     * Setter for the field <code>doesNotContain</code>.
     *
     * @param doesNotContain a {@link String} object.
     * @return a {@link StrFilter} object.
     */
    public StrFilter setDoesNotContain(String doesNotContain) {
        this.doesNotContain = doesNotContain;
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
        StrFilter that = (StrFilter) o;
        return Objects.equals(contains, that.contains) && Objects.equals(doesNotContain, that.doesNotContain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contains, doesNotContain);
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
                (getContains() != null ? "contains=" + getContains() + ", " : "") +
                (getDoesNotContain() != null ? "doesNotContain=" + getDoesNotContain() : "") +
                "]";
    }

}
