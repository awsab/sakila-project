package com.me.learning.framework.service.filter;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import java.time.Instant;
import java.util.Collections;

import org.springframework.format.annotation.DateTimeFormat;


/**
 * Filter class for {@link Instant} type attributes.
 *
 * @see RangeFilter
 */
public class InstantFilter extends RangeFilter<Instant> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for RInstantFilter.</p>
     */
    public InstantFilter() {
    }

    /**
     * <p>Constructor for RInstantFilter.</p>
     *
     * @param filter a {@link InstantFilter} object.
     */
    public InstantFilter(InstantFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     * @return a {@link InstantFilter} object.
     */
    @Override
    public InstantFilter copy() {
        return new InstantFilter(this);
    }


    /**
     * <p>setEquals.</p>
     * @param equals a {@link Instant} object.
     * @return a {@link InstantFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE_TIME)
    public InstantFilter setEquals(Instant equals) {
        if (equals != null) {
            //IL_INFINITE_RECURSIVE_LOOP
            //this.setEquals(equals);
            super.setEquals(equals);
        }
        return this;
    }

    /**
     * <p>setNotEquals.</p>
     * @param notEquals a {@link Instant} object.
     * @return a {@link InstantFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE_TIME)
    public InstantFilter setNotEquals(Instant notEquals) {
        if (notEquals != null) {
            // IL_INFINITE_RECURSIVE_LOOP
            // this.setNotEquals(notEquals);
            super.setNotEquals(notEquals);
        }
        return this;
    }

    /**
     * <p>setGreaterThan.</p>
     *
     * @param equals a {@link Instant} object.
     * @return a {@link InstantFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE_TIME)
    public InstantFilter setGreaterThan(Instant equals) {
        if (equals != null) {
            //IL_INFINITE_RECURSIVE_LOOP
            // this.setGreaterThan(equals);
            super.setGreaterThan(equals);
        }
        return this;
    }

    /**
     * <p>setLessThan.</p>
     *
     * @param equals a {@link Instant} object.
     * @return a {@link InstantFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE_TIME)
    public InstantFilter setLessThan(Instant equals) {
        if (equals != null) {
            //IL_INFINITE_RECURSIVE_LOOP
            // this.setLessThan(equals);
            super.setLessThan(equals);
        }
        return this;
    }

    /**
     * <p>setGreaterThanOrEqual.</p>
     *
     * @param equals a {@link Instant} object.
     * @return a {@link InstantFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE_TIME)
    public InstantFilter setGreaterThanOrEqual(Instant equals) {
        if (equals != null) {
            //IL_INFINITE_RECURSIVE_LOOP
            // this.setGreaterThanOrEqual(equals);
            super.setGreaterThanOrEqual(equals);
        }
        return this;
    }

    /**
     * <p>setLessThanOrEqual.</p>
     *
     * @param equals a {@link RangeFilter} object.
     * @return a {@link InstantFilter} object.
     */
    @Override
    @DateTimeFormat(iso = DATE_TIME)
    public InstantFilter setLessThanOrEqual(Instant equals) {
        if (equals != null) {
            //IL_INFINITE_RECURSIVE_LOOP
            // this.setLessThanOrEqual(equals);
            super.setLessThanOrEqual(equals);
        }
        return this;
    }

    /**
     * <p>setIn.</p>
     *
     * @param equals a {@link Instant} object.
     * @return a {@link InstantFilter} object.
     */
    @DateTimeFormat(iso = DATE_TIME)
    public InstantFilter setIn(Instant equals) {
        if (equals != null) {
            //IL_INFINITE_RECURSIVE_LOOP
             //this.setIn(equals);
            super.setIn (Collections.singletonList (equals));

        }
        return this;
    }

    /**
     * <p>setNotIn.</p>
     *
     * @param equals a {@link Instant} object.
     * @return a {@link InstantFilter} object.
     */
    @DateTimeFormat(iso = DATE_TIME)
    public InstantFilter setNotIn(Instant equals) {
        if (equals != null) {
            //IL_INFINITE_RECURSIVE_LOOP
            // this.setNotIn(equals);
            super.setNotIn (Collections.singletonList (equals));
        }
        return this;
    }
}
