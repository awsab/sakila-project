# Sakila Microservices – Synchronous Interaction Guide

## Purpose

This document reviews the deployable modules in this repository, shows how each one can be used synchronously, and recommends the best Spring approach for service-to-service HTTP calls.

---

## 1. Module audit

> I treated only the deployable service modules as microservices.
>
> `sakila-parent` and `sakila-framework` are support modules, not runtime microservices.

| Module | Role | Current HTTP readiness | Notes |
|---|---|---|---|
| `customer-service` | Customer identity/customer master data | **Ready** | Has controller + explicit port/context path |
| `inventory-service` | Inventory + store ownership | **Mostly ready** | Has controller, but no explicit `server.port` or `context-path` in `application.yaml` |
| `rental-service` | Rental + payment workflow | **Ready** | Has controllers + explicit port/context path |
| `catalog-service` | Catalog domain (`Film`, `Actor`, `Category`, `Language`) | **Not HTTP-ready yet** | Has services/repositories/entities, but no REST controller exposed |

---

## 2. What exists today

### `customer-service`
- Configured app name: `customer-identity-service`
- Configured port: `8081`
- Configured context path: `/customer-identity/api`
- Controller base path: `/api/v1/customers`
- **Actual base URL today**: `http://localhost:8081/customer-identity/api/api/v1/customers`

Existing synchronous endpoints:
- `POST /api/v1/customers`
- `PUT /api/v1/customers/{id}`
- `PATCH /api/v1/customers/{id}`
- `GET /api/v1/customers/{id}`
- `GET /api/v1/customers`
- `GET /api/v1/customers/all`
- `DELETE /api/v1/customers/{id}`
- `GET /api/v1/customers/count`
- `GET /api/v1/customers/exists/{id}`

### `inventory-service`
- Configured app name: `inventory-service`
- No explicit `server.port`
- No explicit `context-path`
- Controller base path: `/api/v1/inventories`
- **Effective base URL if run alone today**: `http://localhost:8080/api/v1/inventories`

Existing synchronous endpoints:
- `POST /api/v1/inventories`
- `PUT /api/v1/inventories/{id}`
- `PATCH /api/v1/inventories/{id}`
- `GET /api/v1/inventories/{id}`
- `GET /api/v1/inventories`
- `GET /api/v1/inventories/all`
- `DELETE /api/v1/inventories/{id}`
- `GET /api/v1/inventories/count`
- `GET /api/v1/inventories/exists/{id}`

### `rental-service`
- Configured app name: `rental-service`
- Configured port: `8082`
- Configured context path: `/rental/api`
- Controller base path: `/api/v1/rentals`
- Payment controller base path: `/api/v1/payments`
- **Actual base URLs today**:
  - `http://localhost:8082/rental/api/api/v1/rentals`
  - `http://localhost:8082/rental/api/api/v1/payments`

Existing synchronous endpoints:

`RentalController`
- `POST /api/v1/rentals/checkout`
- `POST /api/v1/rentals`
- `PUT /api/v1/rentals/{id}`
- `PATCH /api/v1/rentals/{id}`
- `GET /api/v1/rentals/{id}`
- `GET /api/v1/rentals`
- `GET /api/v1/rentals/all`
- `DELETE /api/v1/rentals/{id}`
- `GET /api/v1/rentals/count`
- `GET /api/v1/rentals/exists/{id}`

`PaymentController`
- `POST /api/v1/payments`
- `PUT /api/v1/payments/{id}`
- `PATCH /api/v1/payments/{id}`
- `GET /api/v1/payments/{id}`
- `GET /api/v1/payments`
- `GET /api/v1/payments/all`
- `DELETE /api/v1/payments/{id}`
- `GET /api/v1/payments/count`
- `GET /api/v1/payments/exists/{id}`

### `catalog-service`
- Configured app name: `catalog-service`
- No explicit `server.port`
- No explicit `context-path`
- **Important:** there is currently **no REST controller** in this module.

What exists internally:
- `FilmService`
- `ActorService`
- `CategoryService`
- repositories/entities/mappers/DTOs

So today `catalog-service` is a domain module with service-layer CRUD logic, but **other microservices cannot synchronously call it over HTTP yet**.

---

## 3. Current structural observations

### 3.1 The service boundaries are already visible
From the codebase, the intended ownership appears to be:

- `catalog-service` owns film/catalog data
- `inventory-service` owns inventory + store data
- `customer-service` owns customer identity data
- `rental-service` owns rental + payment workflows

### 3.2 Cross-service references already exist as IDs
Some modules already reference other domains by ID only:

- `inventory-service`
  - `InventoryRequestDto` contains `filmId`
- `rental-service`
  - `RentalRequestDto` contains `inventoryId`, `customerId`, `staffId`
  - `PaymentRequestDto` contains `customerId`, `staffId`, optional `rental`

That is a good microservice direction because it avoids JPA coupling across services.

### 3.3 Remote validation is not implemented yet
For example:
- `RentalServiceImpl` explicitly says cross-service references are stored as plain IDs and **no remote validation happens yet**.
- `InventoryServiceImpl` validates `store` locally, but does **not** validate `filmId` remotely.

So the repo is ready for sync integration conceptually, but the HTTP client layer is still missing.

### 3.4 Port setup is incomplete
Right now:
- `customer-service` = `8081`
- `rental-service` = `8082`
- `inventory-service` = default `8080`
- `catalog-service` = default `8080`

That means `inventory-service` and `catalog-service` will clash if you try to run both at the same time.

---

## 4. Recommended synchronous interaction map

## Guiding rule
Use sync HTTP only for:
- existence checks
- lookup/read operations
- short validation calls required before a local write

Do **not** use sync calls for data ownership transfer. Each service should still own its own writes.

### 4.1 `rental-service` → `customer-service`
**Why:** before creating a rental or checkout, validate that the customer exists and optionally fetch customer details.

**Best current endpoint to call:**
- `GET /api/v1/customers/exists/{id}`
- or `GET /api/v1/customers/{id}` if you need more data

**Recommended use cases:**
- validate `customerId` before `POST /rentals`
- validate `customerId` before `POST /rentals/checkout`
- optionally enrich rental response later with customer summary

**Recommendation:**
- Keep this synchronous.
- Start with `exists/{id}` for write-path validation.
- Use `GET /{id}` only when you really need customer data.

### 4.2 `rental-service` → `inventory-service`
**Why:** before creating a rental, validate that the inventory item exists and is rentable.

**Best current endpoint to call:**
- `GET /api/v1/inventories/exists/{id}`
- or `GET /api/v1/inventories/{id}`

**Recommended use cases:**
- validate `inventoryId` before `POST /rentals`
- validate `inventoryId` before `POST /rentals/checkout`

**Strongly recommended future endpoint:**
- `GET /api/v1/inventories/{id}/availability`

Because existence is not enough. A rental workflow usually needs:
- inventory exists
- inventory belongs to a valid store
- inventory is not already rented out / unavailable

If you keep only `exists/{id}`, business validation will remain too weak.

### 4.3 `inventory-service` → `catalog-service`
**Why:** validate `filmId` before creating/updating inventory.

**Needed endpoint (not exposed yet):**
- `GET /api/v1/films/exists/{id}`
- or `GET /api/v1/films/{id}`

**Recommended use cases:**
- validate `filmId` in `InventoryServiceImpl.create()`
- validate `filmId` in `InventoryServiceImpl.update()`

**Current blocker:**
`catalog-service` has no controller, so this call cannot exist yet.

### 4.4 `customer-service`
At the moment, `customer-service` looks like a mostly standalone master-data service.

**My suggestion:**
- other services may call it synchronously
- it should avoid calling other services unless there is a real business need

### 4.5 `catalog-service`
This should primarily behave as a **provider** service.

**My suggestion:**
- expose read-focused APIs first
- avoid making `catalog-service` call other services synchronously

### 4.6 `rental-service` internal flow
Inside `rental-service`, `Rental` and `Payment` are in the same module.

**Recommendation:**
- keep `Rental ↔ Payment` interaction internal, in-process, and transactional
- do **not** split this particular step into an HTTP call between internal components

That is already how `checkout()` works today, and that is the correct design.

---

## 5. What I would expose per service for sync communication

## `customer-service` (already mostly enough)
Keep:
- `GET /customers/{id}`
- `GET /customers/exists/{id}`

Optionally add later:
- `GET /customers/{id}/summary`

Why add `summary`?
Because downstream services often do not need the full address hierarchy.
A lightweight response can reduce coupling and payload size.

## `inventory-service`
Keep:
- `GET /inventories/{id}`
- `GET /inventories/exists/{id}`

Add:
- `GET /inventories/{id}/availability`
- optionally `GET /inventories/by-film/{filmId}` if a UI or orchestration flow needs it

## `catalog-service`
Expose at least:
- `GET /films/{id}`
- `GET /films/exists/{id}`
- `GET /categories/{id}`
- `GET /actors/{id}`

If this service is mainly for reads, start with **read-only endpoints first**.
You can add create/update APIs later if needed.

## `rental-service`
Keep:
- `POST /rentals/checkout`
- `GET /rentals/{id}`
- `GET /payments/{id}`

Add later only if needed:
- `GET /rentals/by-customer/{customerId}`
- `GET /rentals/by-inventory/{inventoryId}`

That is useful if customer or inventory screens need rental history.

---

## 6. `HttpExchange` or `RestClient`?

## Short answer
**Best choice for this project: use `@HttpExchange` interfaces backed by `RestClient`.**

So if I had to recommend one approach for your repo, I would recommend:

- **Declarative client contracts using `@HttpExchange`**
- **Transport implementation using `RestClient`**

That gives you the best of both worlds.

---

## 7. Why this is the best fit here

### Choose this because your calls are service-to-service and strongly typed
Your downstream calls are not random internet API calls. They are:
- internal
- repeated
- contract-based
- type-safe
- stable within the same system

That is exactly where `@HttpExchange` shines.

### `RestClient` alone is lower-level
Plain `RestClient` is good, but if you use it directly everywhere, you will repeat:
- URLs
- path building
- body mapping
- error handling
- header setup
- timeout config

Across 3–4 microservices, that becomes boilerplate quickly.

### `@HttpExchange` keeps client code clean
With `@HttpExchange`, each downstream service gets a dedicated interface like:
- `CustomerServiceClient`
- `InventoryServiceClient`
- `CatalogServiceClient`

That is easier to read, test, and maintain.

### `RestClient` still matters
`@HttpExchange` does not replace `RestClient` transport configuration.
You still use `RestClient` for:
- base URL
- timeouts
- interceptors
- default headers
- request factory
- observability hooks

So conceptually:

- `@HttpExchange` = client contract
- `RestClient` = HTTP engine

---

## 8. When plain `RestClient` is better

Use plain `RestClient` directly when:
- the URL is very dynamic
- query parameters are highly dynamic
- request/response shape changes often
- you are making only one or two simple calls total
- you need ad hoc HTTP logic rather than a reusable client contract

That is **not** the primary shape of this repository.

For your codebase, the communication pattern is predictable enough that interface-based clients are the better long-term choice.

---

## 9. Recommended client pattern

## Pattern
For every downstream dependency:
1. create a typed interface with `@HttpExchange`
2. back it with a `RestClient`
3. inject it into an application service
4. translate remote failures into `ExternalServiceException`

Example shape:

```java
@HttpExchange(accept = "application/json", contentType = "application/json")
public interface CustomerClient {

    @GetExchange("/api/v1/customers/exists/{id}")
    Boolean existsById(@PathVariable Integer id);

    @GetExchange("/api/v1/customers/{id}")
    CustomerResponseDto findById(@PathVariable Integer id);
}
```

And the proxy configuration pattern:

```java
@Bean
CustomerClient customerClient(CustomerServiceProperties properties) {
    RestClient restClient = RestClient.builder()
            .baseUrl(properties.baseUrl())
            .build();

    HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build();

    return factory.createClient(CustomerClient.class);
}
```

This is the cleanest option for your Spring stack.

---

## 10. Concrete recommendation per module

## For `rental-service`
Create clients for:
- `CustomerClient`
- `InventoryClient`

Use them in:
- `RentalServiceImpl.create()`
- `RentalServiceImpl.update()`
- `RentalServiceImpl.checkout()`

Validation sequence for `checkout()` should become:
1. validate customer exists via `customer-service`
2. validate inventory exists/available via `inventory-service`
3. create local rental row
4. create local payment row

## For `inventory-service`
Create a client for:
- `CatalogClient`

Use it in:
- `InventoryServiceImpl.create()`
- `InventoryServiceImpl.update()`

Validation sequence:
1. validate film exists in `catalog-service`
2. validate local store exists
3. save inventory

## For `customer-service`
No outbound sync client needed right now.

## For `catalog-service`
First expose controller APIs.
Do not build outbound sync dependencies unless a real requirement appears.

---

## 11. Naming and URL cleanup I strongly recommend

Right now:
- `customer-service` context path = `/customer-identity/api`
- controller path = `/api/v1/customers`
- result = `/customer-identity/api/api/v1/customers`

And:
- `rental-service` context path = `/rental/api`
- controller path = `/api/v1/rentals`
- result = `/rental/api/api/v1/rentals`

This works, but it is awkward.

## Better convention
Use:
- service context path only for service identity
- controller path only for API version + resource

Example:
- `customer-service` context path: `/customer-identity`
- controller path: `/api/v1/customers`
- final URL: `/customer-identity/api/v1/customers`

Same for rental/inventory/catalog.

---

## 12. My final recommendation

## Best synchronous strategy
- Keep sync calls **small and explicit**
- Use them mainly for validation and read lookups
- Do not create long call chains
- Keep ownership of writes within the owning service

## Best technical choice
**Use `@HttpExchange` interfaces backed by `RestClient`.**

If I had to standardize this repository, I would do this:
- `rental-service` calls `customer-service` and `inventory-service` through typed `@HttpExchange` clients
- `inventory-service` calls `catalog-service` through a typed `@HttpExchange` client
- `catalog-service` becomes a read-provider service first
- `customer-service` remains a provider service
- `Payment` stays internal to `rental-service`, not HTTP-based

---

## 13. Suggested next steps

1. **Make ports explicit** for `catalog-service` and `inventory-service`
2. **Add REST controllers** to `catalog-service`
3. **Normalize context paths** to remove duplicated `/api`
4. **Create typed downstream clients** using `@HttpExchange` + `RestClient`
5. **Add validation calls** in `rental-service` and `inventory-service`
6. **Add one availability endpoint** in `inventory-service` for rental checks

---

## 14. Practical decision summary

If you want one direct answer:

- For this project, **do not choose plain `RestClient` everywhere by itself**.
- **Standardize on `@HttpExchange` for client interfaces, with `RestClient` underneath.**
- That is the most maintainable synchronous design for your current microservice layout.

