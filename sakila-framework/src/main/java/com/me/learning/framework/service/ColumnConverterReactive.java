package com.me.learning.framework.service;

import org.jspecify.annotations.Nullable;

import org.springframework.core.convert.ConversionService;


/**
 * Interface used in the (@link ConditionBuilder) to help build literal 'value expression' of the Conditions.
 * Converters registered in the (@link DataBaseConfiguration) of the Springboot app
 * will be used for the datatype/data conversion
 *
 */

@FunctionalInterface
public interface ColumnConverterReactive {
    /**
     * Converts the value to the target class with the help of the {@link ConversionService}.
     * @param value to convert.
     * @param target class.
     * @param <T> the parameter for the intended type.
     * @return the value which can be constructed from the input.
     */
    <T> T convert(@Nullable Object value, @Nullable Class<T> target);
}
