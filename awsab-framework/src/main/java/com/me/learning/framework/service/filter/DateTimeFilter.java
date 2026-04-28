package com.me.learning.framework.service.filter;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class DateTimeFilter extends RangeFilter<LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for RLocalDateFilter.</p>
     */
    public DateTimeFilter () {
    }

    /**
     * <p>Constructor for RLocalDateFilter.</p>
     *
     * @param filter a {@link DateTimeFilter} object.
     */
    public DateTimeFilter (DateTimeFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link DateTimeFilter} object.
     */
    @Override
    public DateTimeFilter copy() {
        return new DateTimeFilter (this);
    }

    /**
     * Setter for the field <code>equals</code>.
     *
     * @param equals a {@link RangeFilter} object.
     * @return a {@link DateTimeFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateTimeFilter setEquals(LocalDateTime equals) {
        super.setEquals(equals);
        return this;
    }

    /**
     * Setter for the field <code>notEquals</code>.
     *
     * @param notEquals a {@link RangeFilter} object.
     * @return a {@link DateTimeFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateTimeFilter setNotEquals(LocalDateTime notEquals) {
        super.setNotEquals(notEquals);
        return this;
    }


    /**
     * Setter for the field <code>greaterThan</code>.
     *
     * @param in a {@link List} object.
     * @return a {@link DateTimeFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateTimeFilter setIn(List<LocalDateTime> in) {
        super.setIn(in);
        return this;
    }


    /**
     * Setter for the field <code>notIn</code>.
     *
     * @param notIn a {@link List} object.
     * @return a {@link DateTimeFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE)
    public DateTimeFilter setNotIn(List<LocalDateTime> notIn) {
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
    public DateTimeFilter setGreaterThan(LocalDateTime greaterThan) {
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
    public DateTimeFilter setLessThan(LocalDateTime lessThan) {
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
    public DateTimeFilter setGreaterThanOrEqual(LocalDateTime greaterThanOrEqual) {
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
    public DateTimeFilter setLessThanOrEqual(LocalDateTime lessThanOrEqual) {
        super.setLessThanOrEqual(lessThanOrEqual);
        return this;
    }
}
