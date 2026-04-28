/*
 * Copyright 2016-2025 the original author or authors from the JHipster project.
 *
 * This file is part of the JHipster project, see https://www.jhipster.tech/
 * for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.me.learning.framework.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.SQL;

import com.me.learning.framework.service.filter.BaseFilter;
import com.me.learning.framework.service.filter.BoolFilter;
import com.me.learning.framework.service.filter.DurationFilter;
import com.me.learning.framework.service.filter.InstantFilter;
import com.me.learning.framework.service.filter.RangeFilter;
import com.me.learning.framework.service.filter.StrFilter;
import com.me.learning.framework.service.filter.ZonedDateTimeFilter;


/**
 * Class for constructing org.springframework.data.relational.core.sql.Condition
 * from the Entity Criteria
 *
 */
public class ConditionBuilder {

    private final List<Condition> allFilters = new ArrayList<> ();

    private final ColumnConverterReactive columnConverter;

    public ConditionBuilder (ColumnConverterReactive columnConverter) {
        this.columnConverter = columnConverter;
    }

    /**
     * Method that takes in a filter field and a column to construct a compounded SQL Condition.
     * The built condition can be retrieved using the buildConditions function
     *
     * @param <X>    Field type
     * @param field  The actual Filter field
     * @param column The column for which the condition is constructed
     */
    public <X> void buildFilterConditionForField (BaseFilter<X> field, Column column) {
        if ( field instanceof DurationFilter ) {
            buildRangeConditions ((DurationFilter) field, column, Long.class);
            buildGeneralConditions (field, column, Long.class);
        } else if ( field instanceof ZonedDateTimeFilter ) {
            buildRangeConditions ((ZonedDateTimeFilter) field, column, LocalDateTime.class);
            buildGeneralConditions (field, column, LocalDateTime.class);
        } else if ( field instanceof InstantFilter ) {
            buildRangeConditions ((InstantFilter) field, column, LocalDateTime.class);
            buildGeneralConditions (field, column, LocalDateTime.class);
        } else if ( field instanceof RangeFilter ) {
            buildRangeConditions ((RangeFilter) field, column, null);
            buildGeneralConditions (field, column, null);
        } else if ( field instanceof StrFilter ) {
            buildStringConditions ((StrFilter) field, column);
            buildGeneralConditions (field, column, null);
        } else if ( field instanceof BoolFilter ) {
            buildBooleanConditions (field, column);
        } else {
            buildGeneralConditions (field, column, null);
        }
    }

    private <X extends Comparable<? super X>> void buildRangeConditions (RangeFilter<X> rangeData, Column column, Class<?> targetClass) {
        var converterFunction = columnValueConverter (targetClass);
        if ( rangeData.getGreaterThan () != null ) {
            allFilters.add (Conditions.isGreater (column, SQL.literalOf (converterFunction.apply (rangeData.getGreaterThan ()))));
        }
        if ( rangeData.getLessThan () != null ) {
            allFilters.add (Conditions.isLess (column, SQL.literalOf (converterFunction.apply (rangeData.getLessThan ()))));
        }
        if ( rangeData.getGreaterThanOrEqual () != null ) {
            allFilters.add (
                    Conditions.isGreaterOrEqualTo (column, SQL.literalOf (converterFunction.apply (rangeData.getGreaterThanOrEqual ())))
            );
        }
        if ( rangeData.getLessThanOrEqual () != null ) {
            allFilters.add (Conditions.isLessOrEqualTo (column, SQL.literalOf (converterFunction.apply (rangeData.getLessThanOrEqual ()))));
        }
    }

    private <X> void buildGeneralConditions (BaseFilter<X> generalData, Column column, Class<?> targetClass) {
        Function<X, String> converterFunction = this.<X>columnValueConverter(targetClass);
        addGeneralEqualsCondition(generalData, column, converterFunction);
        addGeneralNotEqualsCondition(generalData, column, converterFunction);
        addGeneralInCondition(generalData, column, converterFunction);
        addGeneralNotInCondition(generalData, column, converterFunction);
        addSpecifiedCondition(generalData, column);
    }

    private <X> void addGeneralEqualsCondition(BaseFilter<X> generalData, Column column, Function<X, String> converterFunction) {
        var value = generalData.getEquals();
        if (value != null) {
            allFilters.add(Conditions.isEqual(column, SQL.literalOf(converterFunction.apply(value))));
        }
    }

    private <X> void addGeneralNotEqualsCondition(BaseFilter<X> generalData, Column column, Function<X, String> converterFunction) {
        var value = generalData.getNotEquals();
        if (value != null) {
            allFilters.add(Conditions.isNotEqual(column, SQL.literalOf(converterFunction.apply(value))));
        }
    }

    private <X> void addGeneralInCondition(BaseFilter<X> generalData, Column column, Function<X, String> converterFunction) {
        var values = generalData.getIn();
        if (values != null && !values.isEmpty()) {
            allFilters.add(
                    Conditions.in(
                            column,
                            values.stream()
                                    .map(eachIn -> SQL.literalOf(converterFunction.apply(eachIn)))
                                    .collect(Collectors.toList())
                    )
            );
        }
    }

    private <X> void addGeneralNotInCondition(BaseFilter<X> generalData, Column column, Function<X, String> converterFunction) {
        var values = generalData.getNotIn();
        if (values != null && !values.isEmpty()) {
            allFilters.add(
                    Conditions.notIn(
                            column,
                            values.stream()
                                    .map(eachNotIn -> SQL.literalOf(converterFunction.apply(eachNotIn)))
                                    .collect(Collectors.toList())
                    )
            );
        }
    }

    private <X> void addSpecifiedCondition(BaseFilter<X> generalData, Column column) {
        var specified = generalData.getSpecified();
        if (specified == null) {
            return;
        }
        allFilters.add(specified ? Conditions.isNull(column).not() : Conditions.isNull(column));
    }

    private void buildStringConditions (StrFilter stringData, Column column) {
        if ( stringData.getContains () != null ) {
            allFilters.add (Conditions.like (column, SQL.literalOf (stringData.getContains ())));
        }
        if ( stringData.getDoesNotContain () != null ) {
            allFilters.add (Conditions.notLike (column, SQL.literalOf (stringData.getDoesNotContain ())));
        }
    }

    private <X> void buildBooleanConditions (BaseFilter<X> generalData, Column column) {
        addBooleanEqualsCondition(generalData, column);
        addBooleanNotEqualsCondition(generalData, column);
        addBooleanInCondition(generalData, column);
        addBooleanNotInCondition(generalData, column);
        addBooleanSpecifiedCondition(generalData, column);
    }

    private <X> void addBooleanEqualsCondition(BaseFilter<X> generalData, Column column) {
        var value = generalData.getEquals();
        if (value != null) {
            allFilters.add(Conditions.isEqual(column, SQL.literalOf(columnConverter.convert(value, Boolean.class))));
        }
    }

    private <X> void addBooleanNotEqualsCondition(BaseFilter<X> generalData, Column column) {
        var value = generalData.getNotEquals();
        if (value != null) {
            allFilters.add(Conditions.isNotEqual(column, SQL.literalOf(columnConverter.convert(value, Boolean.class))));
        }
    }

    private <X> void addBooleanInCondition(BaseFilter<X> generalData, Column column) {
        var values = generalData.getIn();
        if (values != null && !values.isEmpty()) {
            allFilters.add(
                    Conditions.in(
                            column,
                            values.stream()
                                    .map(eachIn -> SQL.literalOf(columnConverter.convert(eachIn, Boolean.class)))
                                    .collect(Collectors.toList())
                    )
            );
        }
    }

    private <X> void addBooleanNotInCondition(BaseFilter<X> generalData, Column column) {
        var values = generalData.getNotIn();
        if (values != null && !values.isEmpty()) {
            allFilters.add(
                    Conditions.notIn(
                            column,
                            values.stream()
                                    .map(eachNotIn -> SQL.literalOf(columnConverter.convert(eachNotIn, Boolean.class)))
                                    .collect(Collectors.toList())
                    )
            );
        }
    }

    private <X> void addBooleanSpecifiedCondition(BaseFilter<X> generalData, Column column) {
        var specified = generalData.getSpecified();
        if (specified == null) {
            return;
        }
        allFilters.add(specified ? Conditions.isNull(column).not() : Conditions.isNull(column));
    }

    private <X> Function<X, String> columnValueConverter (Class<?> targetClass) {
        if ( targetClass != null ) {
            return value -> columnConverter.convert (value, targetClass).toString ();
        } else {
            return Object::toString;
        }
    }

    /**
     * Method that builds and returns the compounded Condition object. This method can be called
     * multiple times as the Conditions are being built.
     *
     * @return returns the compounded Condition object
     */
    public Condition buildConditions () {
        return allFilters
                .stream ()
                .reduce (null, (Condition cumulated, Condition eachCondition) -> {
                    return cumulated != null ? cumulated.and (eachCondition) : eachCondition;
                });
    }
}
