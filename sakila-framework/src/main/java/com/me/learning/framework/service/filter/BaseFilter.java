package com.me.learning.framework.service.filter;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BaseFilter<FILED_T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private FILED_T equalsValue;
    private FILED_T notEquals;
    private Boolean specified;
    private List<FILED_T> in;
    private List<FILED_T> notIn;

    /**
     * <p>Constructor for RFilter.</p>
     */
    public BaseFilter() {
    }

    /**
     * @param filter a {@link BaseFilter} object.
     */
    public BaseFilter(BaseFilter<FILED_T> filter) {
        this.equalsValue = filter.equalsValue;
        this.notEquals = filter.notEquals;
        this.specified = filter.specified;
        this.in = filter.in;
        this.notIn = filter.notIn;
    }

    /**
     *
     * @return a {@link FILED_T} object.
     */
    public BaseFilter<FILED_T> copy() {
        return new BaseFilter<>(this);
    }


    /**
     * <p>Getter for the field <code>equals</code>.</p>
     *
     * @return a FIELD_TYPE object.
     */
    public FILED_T getEquals() {
        return equalsValue;
    }

    /**
     * Setter for the field <code>equals</code>.
     *
     * @param equals a {@link FILED_T} object.
     * @return a {@link BaseFilter} object.
     */
    public BaseFilter<FILED_T> setEquals(FILED_T equals) {
        this.equalsValue = equals;
        return this;
    }

    /**
     * <p>Getter for the field <code>notEquals</code>.</p>
     *
     * @return a FIELD_TYPE object.
     */
    public FILED_T getNotEquals() {
        return notEquals;
    }

    /**
     * Setter for the field <code>notEquals</code>.
     *
     * @param notEquals a {@link FILED_T} object.
     * @return a {@link BaseFilter} object.
     */
    public BaseFilter<FILED_T> setNotEquals(FILED_T notEquals) {
        this.notEquals = notEquals;
        return this;
    }

    /**
     * <p>Getter for the field <code>specified</code>.</p>
     *
     * @return a {@link Boolean} object.
     */
    public Boolean getSpecified() {
        return specified;
    }

    /**
     * Setter for the field <code>specified</code>.
     *
     * @param specified a {@link Boolean} object.
     * @return a {@link BaseFilter} object.
     */
    public BaseFilter<FILED_T> setSpecified(Boolean specified) {
        this.specified = specified;
        return this;
    }

    /**
     * <p>Getter for the field <code>in</code>.</p>
     *
     * @return a {@link List} object.
     */
    public List<FILED_T> getIn() {
        return in;
    }

    /**
     * Setter for the field <code>in</code>.
     *
     * @param in a {@link List} object.
     * @return a {@link BaseFilter} object.
     */
    public BaseFilter<FILED_T> setIn(List<FILED_T> in) {
        //this.in = in;
        this.in = (in == null) ? new ArrayList<>() : in;
        return this;
    }

    /**
     * <p>Getter for the field <code>notIn</code>.</p>
     *
     * @return a {@link List} object.
     */
    public List<FILED_T> getNotIn() {
        return notIn;
    }

    /**
     * Setter for the field <code>notIn</code>.
     *
     * @param notIn a {@link List} object.
     * @return a {@link BaseFilter} object.
     */
    public BaseFilter<FILED_T> setNotIn(List<FILED_T> notIn) {
        //this.notIn = notIn;
        this.notIn = (notIn == null) ? new ArrayList<> () : notIn;
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
        BaseFilter<?> filter = (BaseFilter<?>) o;
        return Objects.equals(equalsValue, filter.equalsValue) &&
                Objects.equals(notEquals, filter.notEquals) &&
                Objects.equals(specified, filter.specified) &&
                Objects.equals(in, filter.in) &&
                Objects.equals(notIn, filter.notIn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equalsValue, notEquals, specified, in, notIn);
    }

    /**
     * <p>getFilterName.</p>
     *
     * @return a {@link String} object.
     */
    protected String getFilterName() {
        return this.getClass().getSimpleName();
    }
}
