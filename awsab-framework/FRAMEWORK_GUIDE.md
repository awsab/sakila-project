# AWSAB Framework Guide

This document is the full usage guide for `awsab-framework`.

It explains:
- what the framework provides,
- when to use each utility/component,
- how to integrate it in a consumer service,
- how to override defaults safely.

## 1) What this framework is

`awsab-framework` is a shared Spring Boot library for AWSAB microservices.

It is **not** a runnable app. It provides reusable infrastructure such as:
- Spring Boot auto-configuration,
- structured API response and error handling,
- filter/criteria utilities for dynamic querying,
- date/time converters,
- logging bootstrap helpers,
- validation and common utility helpers.

Core marker class: `com.me.learning.framework.AwsabFrameworkApplication`.

## 2) Add dependency in consumer service

```xml
<dependency>
    <groupId>com.me.learning.parent</groupId>
    <artifactId>awsab-framework</artifactId>
    <version>${awsab-framework.version}</version>
</dependency>
```

## 3) How auto-configuration works (Boot 3)

### Registration files

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
  - registers `com.me.learning.framework.AwsabFrameworkAutoConfiguration`
- `META-INF/spring.factories`
  - registers early listener `com.me.learning.framework.logging.AwsabLoggingApplicationListener`

### Auto-configured beans

`AwsabFrameworkAutoConfiguration` currently contributes these beans (all override-safe):

- `GlobalExceptionHandler`
- `JavaTimeModule`
- `Jdk8Module`
- `Jackson2ObjectMapperBuilderCustomizer`
- `Clock` (UTC)
- `LinkHeaderUtil`
- `ColumnConverterReactive`
- Date converter beans from `DateConverters`:
  - `LocalDateToDateConverter`
  - `DateToLocalDateConverter`
  - `ZonedDateTimeToDateConverter`
  - `DateToZonedDateTimeConverter`
  - `LocalDateTimeToDateConverter`
  - `DateToLocalDateTimeConverter`
  - `DurationToLongConverter`
  - `LongToDurationConverter`

### Override model

Every framework bean is guarded with `@ConditionalOnMissingBean`.

That means consumer services can replace behavior by declaring their own bean of the same type.

## 4) Logging support

### 4.1 Automatic bootstrap

`AwsabLoggingApplicationListener` runs at startup and can attach JSON logging automatically.

By default:
- JSON console logging: enabled
- Logstash appender: disabled

Consumer properties:

```yaml
awsab:
  logging:
    json-format-enabled: true
    logstash-enabled: false
```

### 4.2 XML include option for custom logback

If a consumer uses its own `logback-spring.xml`, include framework fragment:

```xml
<configuration>
    <include resource="logback-awsab-include.xml"/>
    <!-- your service-specific logger settings -->
</configuration>
```

Provided utility class:
- `com.me.learning.framework.logging.AwsabLogging`
  - `addJSONAppender(...)`
  - `addLogstashAppender(...)` (default destination in utility: `localhost:5000`)

## 5) Standard API response model

Package: `com.me.learning.framework.web.response`

### 5.1 `ApiResponse<T>`

Use for all controller responses to keep consistent payload shape.

Factory methods:
- `ApiResponse.ok(data, message)`
- `ApiResponse.created(data, message)`
- `ApiResponse.noContent()`
- `ApiResponse.error(status, message, apiError)`

Example:

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<CustomerDto>> get(@PathVariable Long id) {
    CustomerDto dto = service.findOne(id);
    return ResponseEntity.ok(ApiResponse.ok(dto, "Customer retrieved"));
}
```

### 5.2 `ApiError`

Structured error block with optional field errors:
- `code`
- `detail`
- `fieldErrors[]`

### 5.3 `PagedResponse<T>`

Wrap Spring Data `Page<T>` into stable pagination payload.

```java
Page<CustomerDto> page = service.search(criteria, pageable);
return ResponseEntity.ok(ApiResponse.ok(PagedResponse.from(page), "Customers retrieved"));
```

## 6) Global exception handling

Handler class:
- `com.me.learning.framework.web.errors.GlobalExceptionHandler`

It returns `ApiResponse.error(...)` consistently for known exceptions.

Supported exception types include:
- `ResourceNotFoundException`
- `ResourceAlreadyExistsException`
- `BusinessRuleViolationException`
- `ExternalServiceException`
- `InvalidRequestException`
- `DuplicateResourceException`
- `DataIntegrityViolationException`
- `ServiceException`
- `BadRequestException`
- `EntityNotFoundException`
- plus standard Spring MVC validation/type errors

### Throwing domain exceptions

```java
if (customer == null) {
    throw new ResourceNotFoundException("Customer not found");
}

if (request.getEmail() == null) {
    throw new InvalidRequestException("Email is required");
}
```

## 7) Web utility helpers

Package: `com.me.learning.framework.web.util`

### 7.1 `HeaderUtil`

Creates alert/failure headers for CRUD operations.

```java
HttpHeaders headers = HeaderUtil.createEntityCreationAlert(
    "customer-service", false, "customer", String.valueOf(saved.getId())
);
```

### 7.2 `PaginationUtil` and `LinkHeaderUtil`

Builds RFC5988 pagination headers (`Link`, `X-Total-Count`).

```java
HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
    UriComponentsBuilder.fromPath("/api/customers"), page
);
```

### 7.3 `ResponseUtil`

Convenience wrapper for `Optional` responses.

```java
return ResponseUtil.wrapOrNotFound(optionalDto);
```

### 7.4 `PageUtil`

Creates a pageable sublist from an in-memory list.

```java
Page<CustomerDto> page = PageUtil.createPageableFromList(items, pageable);
```

## 8) Filter and query utilities

Package: `com.me.learning.framework.service` and `.service.filter`

### 8.1 Criteria contract

- `BaseCriteria` with `copy()` for immutable-safe cloning in criteria classes.

### 8.2 Filter classes

Base and specialized filters:
- `BaseFilter<T>` (equals, notEquals, specified, in, notIn)
- `RangeFilter<T>` (greaterThan, lessThan, etc.)
- typed filters: `LongFilter`, `IntFilter`, `DateFilter`, `DateTimeFilter`,
  `ZonedDateTimeFilter`, `InstantFilter`, `DurationFilter`, `StrFilter`, `BoolFilter`, etc.

Typical criteria class:

```java
public class CustomerCriteria implements BaseCriteria {
    private LongFilter id;
    private StrFilter email;
    private BoolFilter active;
    // getters/setters/copy
}
```

### 8.3 `BaseQueryService<ENTITY>` (JPA Specification)

Use this as base class for JPA query services to build `Specification`s quickly.

Common helpers:
- `buildSpecification(...)`
- `buildStringSpecification(...)`
- `buildRangeSpecification(...)`
- `buildReferringEntitySpecification(...)`

### 8.4 `ConditionBuilder` (Spring Data Relational SQL)

Builds SQL DSL `Condition` objects from filter objects.

Workflow:
1. create `ConditionBuilder` with `ColumnConverterReactive`,
2. call `buildFilterConditionForField(filter, column)` for each filter,
3. call `buildConditions()` to get compound `AND` condition.

```java
ConditionBuilder cb = new ConditionBuilder(columnConverter);
cb.buildFilterConditionForField(criteria.getEmail(), SQL.column("email"));
cb.buildFilterConditionForField(criteria.getActive(), SQL.column("active"));
Condition where = cb.buildConditions();
```

### 8.5 `ColumnConverterReactive`

Small interface used by `ConditionBuilder` to convert values to target DB-friendly types.

Framework auto-config provides a default implementation based on Spring `ConversionService`.

## 9) Date and time conversion helpers

Class: `DateConverters`

Contains converter singletons for:
- `LocalDate <-> Date`
- `LocalDateTime <-> Date`
- `ZonedDateTime <-> Date`
- `Duration <-> Long` (nanos)

Most services can rely on auto-registered converter beans from auto-configuration.

## 10) Mapping utilities

Package: `com.me.learning.framework.service.mapper`

### `BaseEntityMapper<D, E>`

MapStruct-friendly base interface with:
- `toEntity(D)` / `toDto(E)`
- list mapping methods
- `partialUpdate(@MappingTarget E entity, D dto)` ignoring null properties

Example:

```java
@Mapper(componentModel = "spring")
public interface CustomerMapper extends BaseEntityMapper<CustomerDto, Customer> {
}
```

## 11) Validation utilities

Package: `com.me.learning.framework.service.validation`

### `@NotNullAndBlank`

Custom annotation + validator to enforce non-null and non-blank string values.

```java
public class CreateCustomerRequest {
    @NotNullAndBlank
    private String firstName;
}
```

## 12) Generic helpers

Class: `AwsabUtils`

Useful static methods:
- null/blank guard helpers (`defaultIfBlank`, `requireNonBlank`, `requireNonEmpty`)
- UUID validation/parsing (`parseUuidOrNull`, `isValidUuid`)
- common validation (`isValidEmail`, `isValidPhoneNumber`, etc.)
- string masking/truncation (`mask`, `truncate`)
- `isEmpty` helpers for collections and maps

## 13) Profile helpers

Package: `com.me.learning.framework.config`

- `AwsabProfileConstants`: profile constants (`dev`, `sit`, `uat`, `prod`, ...)
- `DefaultProfileConfig`: helper to set default Spring profile programmatically

## 14) Recommended consumer integration checklist

1. Add `awsab-framework` dependency.
2. Keep your controllers returning `ApiResponse<T>`.
3. Throw framework exceptions for known business error categories.
4. Use filter-based criteria classes for dynamic search endpoints.
5. Use `BaseEntityMapper` for DTO/entity mapping.
6. If you use custom logback XML, include `logback-awsab-include.xml` once.
7. Override framework beans only when your service truly needs custom behavior.

## 15) Quick troubleshooting

- **Duplicate JSON logs**: ensure you are not registering both your own JSON appender and framework include twice.
- **Expected bean missing**: check whether your app defines an override bean (because framework beans are conditional).
- **Auto-config doubts**: start with `debug=true` and inspect Spring condition report.

---

If you add new utility classes to `awsab-framework`, update this guide in the same PR so consumer teams always have one current reference.

