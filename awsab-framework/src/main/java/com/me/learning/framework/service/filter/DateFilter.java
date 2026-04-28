package com.me.learning.framework.service.filter;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Filter class for {@link LocalDate} type attributes.
 *
 * @see RangeFilter
 */
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class DateFilter extends RangeFilter<LocalDate> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for RLocalDateFilter.</p>
     */
    public DateFilter() {
    }

    /**
     * <p>Constructor for RLocalDateFilter.</p>
     *
     * @param filter a {@link DateFilter} object.
     */
    public DateFilter(DateFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link DateFilter} object.
     */
    @Override
    public DateFilter copy() {
        return new DateFilter(this);
    }

    /**
     * Setter for the field <code>equals</code>.
     *
     * @param equals a {@link RangeFilter} object.
     * @return a {@link DateFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateFilter setEquals(LocalDate equals) {
        super.setEquals(equals);
        return this;
    }

    /**
     * Setter for the field <code>notEquals</code>.
     *
     * @param notEquals a {@link RangeFilter} object.
     * @return a {@link DateFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateFilter setNotEquals(LocalDate notEquals) {
        super.setNotEquals(notEquals);
        return this;
    }


    /**
     * Setter for the field <code>greaterThan</code>.
     *
     * @param in a {@link List} object.
     * @return a {@link DateFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateFilter setIn(List<LocalDate> in) {
        super.setIn(in);
        return this;
    }


    /**
     * Setter for the field <code>notIn</code>.
     *
     * @param notIn a {@link List} object.
     * @return a {@link DateFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateFilter setNotIn(List<LocalDate> notIn) {
        super.setNotIn(notIn);
        return this;
    }

    /**
     * Setter for the field <code>greaterThan</code>.
     * @param greaterThan a {@link RangeFilter} object.
     * @return a {@link RangeFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateFilter setGreaterThan(LocalDate greaterThan) {
        super.setGreaterThan(greaterThan);
        return this;
    }

    /**
     * Setter for the field <code>lessThan</code>.
     * @param lessThan a {@link RangeFilter} object.
     * @return a {@link RangeFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateFilter setLessThan(LocalDate lessThan) {
        super.setLessThan(lessThan);
        return this;
    }

    /**
     * Setter for the field <code>greaterThanOrEqual</code>.
     * @param greaterThanOrEqual a {@link RangeFilter} object.
     * @return a {@link RangeFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateFilter setGreaterThanOrEqual(LocalDate greaterThanOrEqual) {
        super.setGreaterThanOrEqual(greaterThanOrEqual);
        return this;
    }

    /**
     * Setter for the field <code>lessThanOrEqual</code>.
     * @param lessThanOrEqual a {@link RangeFilter} object.
     * @return a {@link RangeFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateFilter setLessThanOrEqual(LocalDate lessThanOrEqual) {
        super.setLessThanOrEqual(lessThanOrEqual);
        return this;
    }
}
