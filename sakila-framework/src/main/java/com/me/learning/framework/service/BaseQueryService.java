package com.me.learning.framework.service;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

import org.jspecify.annotations.Nullable;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.me.learning.framework.service.filter.BaseFilter;
import com.me.learning.framework.service.filter.RangeFilter;
import com.me.learning.framework.service.filter.StrFilter;

@Transactional (readOnly = true)
public abstract class BaseQueryService<ENTITY> {

    /**
     * Helper function to return a specification for filtering on a single field, where equality, and null/non-null
     * conditions are supported.
     *
     * @param filter the individual attribute filter coming from the frontend.
     * @param field  the JPA static metamodel representing the field.
     * @param <X>    The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <X> Specification<ENTITY> buildSpecification(@Nullable BaseFilter<X> filter, SingularAttribute<? super ENTITY, X> field) {
        return buildSpecification(filter, root -> root.get(field));
    }

    /**
     * Helper function to return a specification for filtering on a single field, where equality, and null/non-null
     * conditions are supported.
     *
     * @param filter            the individual attribute filter coming from the frontend.
     * @param metaclassFunction the function, which navigates from the current entity to a column, for which the filter applies.
     * @param <X>               The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <X> Specification<ENTITY> buildSpecification(
            @Nullable BaseFilter<X> filter,
            Function<Root<ENTITY>, Expression<X>> metaclassFunction
    ) {
        if (filter == null) {
            return null;
        } else if (filter.getEquals() != null) {
            return equalsSpecification(metaclassFunction, filter.getEquals());
        } else if (filter.getIn() != null) {
            return valueIn(metaclassFunction, filter.getIn());
        } else if (filter.getNotIn() != null) {
            return valueNotIn(metaclassFunction, filter.getNotIn());
        } else if (filter.getNotEquals() != null) {
            return notEqualsSpecification(metaclassFunction, filter.getNotEquals());
        } else if (filter.getSpecified() != null) {
            return byFieldSpecified(metaclassFunction, filter.getSpecified());
        }
        return null;
    }

    /**
     * Helper function to return a specification for filtering on a {@link java.lang.String} field, where equality, containment,
     * and null/non-null conditions are supported.
     *
     * @param filter the individual attribute filter coming from the frontend.
     * @param field  the JPA static metamodel representing the field.
     * @return a Specification
     */
    protected Specification<ENTITY> buildStringSpecification(
            @Nullable StrFilter filter,
            SingularAttribute<? super ENTITY, String> field
    ) {
        return buildSpecification(filter, root -> root.get(field));
    }

    /**
     * Helper function to return a specification for filtering on a {@link java.lang.String} field, where equality, containment,
     * and null/non-null conditions are supported.
     *
     * @param filter            the individual attribute filter coming from the frontend.
     * @param metaclassFunction lambda, which based on a Root&lt;ENTITY&gt; returns Expression - basicaly picks a column
     * @return a Specification
     */
    protected Specification<ENTITY> buildSpecification(
            @Nullable StrFilter filter,
            Function<Root<ENTITY>, Expression<String>> metaclassFunction
    ) {
        if (filter == null) {
            return null;
        } else if (filter.getEquals() != null) {
            return equalsSpecification(metaclassFunction, filter.getEquals());
        } else if (filter.getIn() != null) {
            return valueIn(metaclassFunction, filter.getIn());
        } else if (filter.getNotIn() != null) {
            return valueNotIn(metaclassFunction, filter.getNotIn());
        } else if (filter.getContains() != null) {
            return likeUpperSpecification(metaclassFunction, filter.getContains());
        } else if (filter.getDoesNotContain() != null) {
            return doesNotContainSpecification(metaclassFunction, filter.getDoesNotContain());
        } else if (filter.getNotEquals() != null) {
            return notEqualsSpecification(metaclassFunction, filter.getNotEquals());
        } else if (filter.getSpecified() != null) {
            return byFieldSpecified(metaclassFunction, filter.getSpecified());
        }
        return null;
    }

    /**
     * Helper function to return a specification for filtering on a single {@link java.lang.Comparable}, where equality, less
     * than, greater than and less-than-or-equal-to and greater-than-or-equal-to and null/non-null conditions are
     * supported.
     *
     * @param <X>    The type of the attribute which is filtered.
     * @param filter the individual attribute filter coming from the frontend.
     * @param field  the JPA static metamodel representing the field.
     * @return a Specification
     */
    protected <X extends Comparable<? super X>> Specification<ENTITY> buildRangeSpecification(
            @Nullable RangeFilter<X> filter,
            SingularAttribute<? super ENTITY, X> field
    ) {
        return buildSpecification(filter, root -> root.get(field));
    }

    /**
     * Helper function to return a specification for filtering on a single {@link java.lang.Comparable}, where equality, less
     * than, greater than and less-than-or-equal-to and greater-than-or-equal-to and null/non-null conditions are
     * supported.
     *
     * @param <X>               The type of the attribute which is filtered.
     * @param filter            the individual attribute filter coming from the frontend.
     * @param metaclassFunction lambda, which based on a Root&lt;ENTITY&gt; returns Expression - basicaly picks a column
     * @return a Specification
     */
    protected <X extends Comparable<? super X>> Specification<ENTITY> buildSpecification(
            @Nullable RangeFilter<X> filter,
            Function<Root<ENTITY>, Expression<X>> metaclassFunction
    ) {
        if (filter == null) {
            return null;
        } else if (filter.getEquals() != null) {
            return equalsSpecification(metaclassFunction, filter.getEquals());
        } else if (filter.getIn() != null) {
            return valueIn(metaclassFunction, filter.getIn());
        }

        Specification<ENTITY> result = Specification.unrestricted();
        result = andIfPresent(result, filter.getSpecified(), specified -> byFieldSpecified(metaclassFunction, specified));
        result = andIfPresent(result, filter.getNotEquals(), notEquals -> notEqualsSpecification(metaclassFunction, notEquals));
        result = andIfPresent(result, filter.getNotIn(), notIn -> valueNotIn(metaclassFunction, notIn));
        result = andIfPresent(result, filter.getGreaterThan(), greater -> greaterThan(metaclassFunction, greater));
        result = andIfPresent(
                result,
                filter.getGreaterThanOrEqual(),
                greaterOrEqual -> greaterThanOrEqualTo(metaclassFunction, greaterOrEqual)
        );
        result = andIfPresent(result, filter.getLessThan(), less -> lessThan(metaclassFunction, less));
        result = andIfPresent(result, filter.getLessThanOrEqual(), lessOrEqual -> lessThanOrEqualTo(metaclassFunction, lessOrEqual));
        return result;
    }

    /**
     * Helper function to return a specification for filtering on one-to-one or many-to-one reference. Usage:
     * <pre>
     *   Specification&lt;Employee&gt; specByProjectId = buildReferringEntitySpecification(criteria.getProjectId(),
     * Employee_.project, Project_.id);
     *   Specification&lt;Employee&gt; specByProjectName = buildReferringEntitySpecification(criteria.getProjectName(),
     * Employee_.project, Project_.name);
     * </pre>
     *
     * @param filter     the filter object which contains a value, which needs to match or a flag if nullness is
     *                   checked.
     * @param reference  the attribute of the static metamodel for the referring entity.
     * @param valueField the attribute of the static metamodel of the referred entity, where the equality should be
     *                   checked.
     * @param <OTHER>    The type of the referenced entity.
     * @param <X>        The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <OTHER, X> Specification<ENTITY> buildReferringEntitySpecification(
            @Nullable BaseFilter<X> filter,
            SingularAttribute<? super ENTITY, OTHER> reference,
            SingularAttribute<? super OTHER, X> valueField
    ) {
        return buildSpecification(filter, root -> root.get(reference).get(valueField));
    }

    /**
     * Helper function to return a specification for filtering on one-to-many or many-to-many reference. Usage:
     * <pre>
     *   Specification&lt;Employee&gt; specByEmployeeId = buildReferringEntitySpecification(criteria.getEmployeId(),
     * Project_.employees, Employee_.id);
     *   Specification&lt;Employee&gt; specByEmployeeName = buildReferringEntitySpecification(criteria.getEmployeName(),
     * Project_.project, Project_.name);
     * </pre>
     *
     * @param filter     the filter object which contains a value, which needs to match or a flag if emptiness is
     *                   checked.
     * @param reference  the attribute of the static metamodel for the referring entity.
     * @param valueField the attribute of the static metamodel of the referred entity, where the equality should be
     *                   checked.
     * @param <OTHER>    The type of the referenced entity.
     * @param <X>        The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <OTHER, X> Specification<ENTITY> buildReferringEntitySpecification(
            @Nullable BaseFilter<X> filter,
            SetAttribute<ENTITY, OTHER> reference,
            SingularAttribute<OTHER, X> valueField
    ) {
        return buildReferringEntitySpecification(filter, root -> root.join(reference), entity -> entity.get(valueField));
    }

    /**
     * Helper function to return a specification for filtering on one-to-many or many-to-many reference.Usage:<pre>
     *   Specification&lt;Employee&gt; specByEmployeeId = buildReferringEntitySpecification(
     *          criteria.getEmployeId(),
     *          root -&gt; root.get(Project_.company).join(Company_.employees),
     *          entity -&gt; entity.get(Employee_.id));
     *   Specification&lt;Employee&gt; specByProjectName = buildReferringEntitySpecification(
     *          criteria.getProjectName(),
     *          root -&gt; root.get(Project_.project)
     *          entity -&gt; entity.get(Project_.name));
     * </pre>
     *
     * @param filter           the filter object which contains a value, which needs to match or a flag if emptiness is
     *                         checked.
     * @param functionToEntity the function, which joins he current entity to the entity set, on which the filtering is applied.
     * @param entityToColumn   the function, which of the static metamodel of the referred entity, where the equality should be
     *                         checked.
     * @param <OTHER>          The type of the referenced entity.
     * @param <MISC>           The type of the entity which is the last before the OTHER in the chain.
     * @param <X>              The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <OTHER, MISC, X> Specification<ENTITY> buildReferringEntitySpecification(
            BaseFilter<X> filter,
            Function<Root<ENTITY>, SetJoin<MISC, OTHER>> functionToEntity,
            Function<SetJoin<MISC, OTHER>, Expression<X>> entityToColumn
    ) {
        if (filter == null) {
            return null;
        } else if (filter.getEquals() != null) {
            return equalsSpecification(functionToEntity.andThen(entityToColumn), filter.getEquals());
        } else if (filter.getSpecified() != null) {
            // Interestingly, 'functionToEntity' doesn't work, we need the longer lambda formula
            return byFieldSpecified(functionToEntity::apply, filter.getSpecified());
        }
        return null;
    }

    /**
     * Helper function to return a specification for filtering on one-to-many or many-to-many reference.Where equality, less
     * than, greater than and less-than-or-equal-to and greater-than-or-equal-to and null/non-null conditions are
     * supported. Usage:
     * <pre>
     *   Specification&lt;Employee&gt; specByEmployeeId = buildReferringEntitySpecification(criteria.getEmployeId(),
     * Project_.employees, Employee_.id);
     *   Specification&lt;Employee&gt; specByEmployeeName = buildReferringEntitySpecification(criteria.getEmployeName(),
     * Project_.project, Project_.name);
     * </pre>
     *
     * @param <X>        The type of the attribute which is filtered.
     * @param filter     the filter object which contains a value, which needs to match or a flag if emptiness is
     *                   checked.
     * @param reference  the attribute of the static metamodel for the referring entity.
     * @param valueField the attribute of the static metamodel of the referred entity, where the equality should be
     *                   checked.
     * @param <OTHER>    The type of the referenced entity.
     * @return a Specification
     */
    protected <OTHER, X extends Comparable<? super X>> Specification<ENTITY> buildReferringEntitySpecification(
            @Nullable RangeFilter<X> filter,
            SetAttribute<ENTITY, OTHER> reference,
            SingularAttribute<OTHER, X> valueField
    ) {
        return buildReferringEntitySpecification(filter, root -> root.join(reference), entity -> entity.get(valueField));
    }

    /**
     * Helper function to return a specification for filtering on one-to-many or many-to-many reference.Where equality, less
     * than, greater than and less-than-or-equal-to and greater-than-or-equal-to and null/non-null conditions are
     * supported. Usage:
     * <pre><code>
     *   Specification&lt;Employee&gt; specByEmployeeId = buildReferringEntitySpecification(
     *          criteria.getEmployeId(),
     *          root -&gt; root.get(Project_.company).join(Company_.employees),
     *          entity -&gt; entity.get(Employee_.id));
     *   Specification&lt;Employee&gt; specByProjectName = buildReferringEntitySpecification(
     *          criteria.getProjectName(),
     *          root -&gt; root.get(Project_.project)
     *          entity -&gt; entity.get(Project_.name));
     * </code>
     * </pre>
     *
     * @param <X>              The type of the attribute which is filtered.
     * @param filter           the filter object which contains a value, which needs to match or a flag if emptiness is
     *                         checked.
     * @param functionToEntity the function, which joins he current entity to the entity set, on which the filtering is applied.
     * @param entityToColumn   the function, which of the static metamodel of the referred entity, where the equality should be
     *                         checked.
     * @param <OTHER>          The type of the referenced entity.
     * @param <MISC>           The type of the entity which is the last before the OTHER in the chain.
     * @return a Specification
     */
    protected <OTHER, MISC, X extends Comparable<? super X>> Specification<ENTITY> buildReferringEntitySpecification(
            @Nullable RangeFilter<X> filter,
            Function<Root<ENTITY>, SetJoin<MISC, OTHER>> functionToEntity,
            Function<SetJoin<MISC, OTHER>, Expression<X>> entityToColumn
    ) {
        if (filter == null) {
            return null;
        }
        Function<Root<ENTITY>, Expression<X>> fused = functionToEntity.andThen(entityToColumn);
        if (filter.getEquals() != null) {
            return equalsSpecification(fused, filter.getEquals());
        } else if (filter.getIn() != null) {
            return valueIn(fused, filter.getIn());
        }
        Specification<ENTITY> result = Specification.unrestricted();
        // Interestingly, 'functionToEntity' doesn't work, we need the longer lambda formula.
        result = andIfPresent(result, filter.getSpecified(), specified -> byFieldSpecified(functionToEntity::apply, specified));
        result = andIfPresent(result, filter.getNotEquals(), notEquals -> notEqualsSpecification(fused, notEquals));
        result = andIfPresent(result, filter.getNotIn(), notIn -> valueNotIn(fused, notIn));
        result = andIfPresent(result, filter.getGreaterThan(), greater -> greaterThan(fused, greater));
        result = andIfPresent(result, filter.getGreaterThanOrEqual(), greaterOrEqual -> greaterThanOrEqualTo(fused, greaterOrEqual));
        result = andIfPresent(result, filter.getLessThan(), less -> lessThan(fused, less));
        result = andIfPresent(result, filter.getLessThanOrEqual(), lessOrEqual -> lessThanOrEqualTo(fused, lessOrEqual));
        return result;
    }

    private <X> Specification<ENTITY> andIfPresent(
            Specification<ENTITY> base,
            @Nullable X value,
            Function<X, Specification<ENTITY>> specificationBuilder
    ) {
        return value == null ? base : base.and(specificationBuilder.apply(value));
    }

    /**
     * Generic method, which based on a Root&lt;ENTITY&gt; returns an Expression which type is the same as the given 'value' type.
     *
     * @param metaclassFunction function which returns the column which is used for filtering.
     * @param value             the actual value to filter for.
     * @param <X>              The type of the attribute which is filtered.
     * @return a Specification.
     */
    protected <X> Specification<ENTITY> equalsSpecification(Function<Root<ENTITY>, Expression<X>> metaclassFunction, X value) {
        return (root, query, builder) -> builder.equal(metaclassFunction.apply(root), value);
    }

    /**
     * Generic method, which based on a Root&lt;ENTITY&gt; returns an Expression which type is the same as the given 'value' type.
     *
     * @param metaclassFunction function which returns the column which is used for filtering.
     * @param value             the actual value to exclude for.
     * @param <X>              The type of the attribute which is filtered.
     * @return a Specification.
     */
    protected <X> Specification<ENTITY> notEqualsSpecification(Function<Root<ENTITY>, Expression<X>> metaclassFunction, X value) {
        return (root, query, builder) -> builder.not(builder.equal(metaclassFunction.apply(root), value));
    }

    /**
     * <p>likeUpperSpecification.</p>
     *
     * @param metaclassFunction a {@link java.util.function.Function} object.
     * @param value a {@link java.lang.String} object.
     * @return a {@link org.springframework.data.jpa.domain.Specification} object.
     */
    protected Specification<ENTITY> likeUpperSpecification(Function<Root<ENTITY>, Expression<String>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.like(builder.upper(metaclassFunction.apply(root)), wrapLikeQuery(value));
    }

    /**
     * <p>doesNotContainSpecification.</p>
     *
     * @param metaclassFunction a {@link java.util.function.Function} object.
     * @param value a {@link java.lang.String} object.
     * @return a {@link org.springframework.data.jpa.domain.Specification} object.
     */
    protected Specification<ENTITY> doesNotContainSpecification(
            Function<Root<ENTITY>, Expression<String>> metaclassFunction,
            String value
    ) {
        return (root, query, builder) -> builder.not(builder.like(builder.upper(metaclassFunction.apply(root)), wrapLikeQuery(value)));
    }

    /**
     * <p>byFieldSpecified.</p>
     *
     * @param metaclassFunction a {@link java.util.function.Function} object.
     * @param specified a boolean.
     * @param <X> a X object.
     * @return a {@link org.springframework.data.jpa.domain.Specification} object.
     */
    protected <X> Specification<ENTITY> byFieldSpecified(Function<Root<ENTITY>, Expression<X>> metaclassFunction, boolean specified) {
        return specified
                ? (root, query, builder) -> builder.isNotNull(metaclassFunction.apply(root))
                : (root, query, builder) -> builder.isNull(metaclassFunction.apply(root));
    }

    /**
     * <p>byFieldEmptiness.</p>
     *
     * @param metaclassFunction a {@link java.util.function.Function} object.
     * @param specified a boolean.
     * @param <X> a X object.
     * @return a {@link org.springframework.data.jpa.domain.Specification} object.
     */
    protected <X> Specification<ENTITY> byFieldEmptiness(Function<Root<ENTITY>, Expression<Set<X>>> metaclassFunction, boolean specified) {
        return specified
                ? (root, query, builder) -> builder.isNotEmpty(metaclassFunction.apply(root))
                : (root, query, builder) -> builder.isEmpty(metaclassFunction.apply(root));
    }

    /**
     * <p>valueIn.</p>
     *
     * @param metaclassFunction a {@link java.util.function.Function} object.
     * @param values a {@link java.util.Collection} object.
     * @param <X> a X object.
     * @return a {@link org.springframework.data.jpa.domain.Specification} object.
     */
    protected <X> Specification<ENTITY> valueIn(Function<Root<ENTITY>, Expression<X>> metaclassFunction, Collection<X> values) {
        return (root, query, builder) -> {
            In<X> in = builder.in(metaclassFunction.apply(root));
            for (X value : values) {
                in = in.value(value);
            }
            return in;
        };
    }

    /**
     * <p>valueNotIn.</p>
     *
     * @param metaclassFunction a {@link java.util.function.Function} object.
     * @param values a {@link java.util.Collection} object.
     * @param <X> a X object.
     * @return a {@link org.springframework.data.jpa.domain.Specification} object.
     */
    protected <X> Specification<ENTITY> valueNotIn(Function<Root<ENTITY>, Expression<X>> metaclassFunction, Collection<X> values) {
        return (root, query, builder) -> {
            In<X> in = builder.in(metaclassFunction.apply(root));
            for (X value : values) {
                in = in.value(value);
            }
            return builder.not(in);
        };
    }

    /**
     * <p>greaterThanOrEqualTo.</p>
     *
     * @param metaclassFunction a {@link java.util.function.Function} object.
     * @param value a X object.
     * @param <X> a X object.
     * @return a {@link org.springframework.data.jpa.domain.Specification} object.
     */
    protected <X extends Comparable<? super X>> Specification<ENTITY> greaterThanOrEqualTo(
            Function<Root<ENTITY>, Expression<X>> metaclassFunction,
            X value
    ) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(metaclassFunction.apply(root), value);
    }

    /**
     * <p>greaterThan.</p>
     *
     * @param metaclassFunction a {@link java.util.function.Function} object.
     * @param value a X object.
     * @param <X> a X object.
     * @return a {@link org.springframework.data.jpa.domain.Specification} object.
     */
    protected <X extends Comparable<? super X>> Specification<ENTITY> greaterThan(
            Function<Root<ENTITY>, Expression<X>> metaclassFunction,
            X value
    ) {
        return (root, query, builder) -> builder.greaterThan(metaclassFunction.apply(root), value);
    }

    /**
     * <p>lessThanOrEqualTo.</p>
     *
     * @param metaclassFunction a {@link java.util.function.Function} object.
     * @param value a X object.
     * @param <X> a X object.
     * @return a {@link org.springframework.data.jpa.domain.Specification} object.
     */
    protected <X extends Comparable<? super X>> Specification<ENTITY> lessThanOrEqualTo(
            Function<Root<ENTITY>, Expression<X>> metaclassFunction,
            X value
    ) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(metaclassFunction.apply(root), value);
    }

    /**
     * <p>lessThan.</p>
     *
     * @param metaclassFunction a {@link java.util.function.Function} object.
     * @param value a X object.
     * @param <X> a X object.
     * @return a {@link org.springframework.data.jpa.domain.Specification} object.
     */
    protected <X extends Comparable<? super X>> Specification<ENTITY> lessThan(
            Function<Root<ENTITY>, Expression<X>> metaclassFunction,
            X value
    ) {
        return (root, query, builder) -> builder.lessThan(metaclassFunction.apply(root), value);
    }

    /**
     * <p>wrapLikeQuery.</p>
     *
     * @param txt a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    protected String wrapLikeQuery(String txt) {
        return "%" + txt.toUpperCase(Locale.ROOT) + '%';
    }

    /**
     * <p>distinct.</p>
     *
     * @param distinct a boolean.
     * @return a {@link org.springframework.data.jpa.domain.Specification} object.
     */
    protected Specification<ENTITY> distinct(boolean distinct) {
        return (root, query, cb) -> {
            query.distinct(distinct);
            return null;
        };
    }

}
