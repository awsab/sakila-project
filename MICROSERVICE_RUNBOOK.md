# 🏗️ Sakila Microservice Module Runbook

> **Purpose** — End-to-end guide to scaffold a brand-new microservice module in this project,
> following the exact patterns established in `inventory-service` and `customer-service`.
>
> ✅ **Two PowerShell automation scripts do 90 % of the work for you.**
> This runbook explains when and how to call them, and what you must still do manually.

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Scripts at a Glance](#2-scripts-at-a-glance)
3. [Port & Package Registry](#3-port--package-registry)
4. [Import-Order Rule (Checkstyle)](#4-import-order-rule-checkstyle)
5. [PHASE 1 — Scaffold the Module](#5-phase-1--scaffold-the-module)
6. [PHASE 2 — Create JPA Entities (Manual)](#6-phase-2--create-jpa-entities-manual)
7. [PHASE 3 — Generate Entity Boilerplate](#7-phase-3--generate-entity-boilerplate)
8. [PHASE 4 — Wire Up the Remaining TODO Items](#8-phase-4--wire-up-the-remaining-todo-items)
9. [PHASE 5 — Verify the Build](#9-phase-5--verify-the-build)
10. [Full Worked Example — film-service](#10-full-worked-example--film-service)
11. [JPA Entity Checklist](#11-jpa-entity-checklist)
12. [DTO Validation Reference](#12-dto-validation-reference)
13. [MapStruct FK-Mapping Reference](#13-mapstruct-fk-mapping-reference)
14. [Naming Conventions Cheat Sheet](#14-naming-conventions-cheat-sheet)
15. [Troubleshooting](#15-troubleshooting)

---

## 1. Architecture Overview

Every microservice module follows this identical layout:

```
<module-name>/
├── pom.xml
├── mvnw  /  mvnw.cmd
└── src/
    ├── main/
    │   ├── java/com/me/learning/parent/<pkg>/
    │   │   ├── <PascalName>Application.java
    │   │   ├── config/
    │   │   │   ├── CacheConfig.java
    │   │   │   ├── LoggingInterceptor.java
    │   │   │   ├── OpenAPIConfig.java
    │   │   │   └── WebMvcConfig.java
    │   │   ├── controller/
    │   │   │   └── <Entity>Controller.java      ← one per entity
    │   │   ├── dto/
    │   │   │   ├── <Entity>Request.java         ← create payload
    │   │   │   ├── <Entity>UpdateRequest.java   ← update / patch payload
    │   │   │   └── <Entity>Response.java        ← read / response
    │   │   ├── entity/
    │   │   │   └── <Entity>.java                ← @Entity (MANUAL)
    │   │   ├── exception/
    │   │   │   ├── DuplicateResourceException.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   ├── InvalidRequestException.java
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   └── ServiceException.java
    │   │   ├── mapper/
    │   │   │   └── <Entity>Mapper.java
    │   │   ├── repository/
    │   │   │   └── <Entity>Repository.java
    │   │   └── service/
    │   │       ├── <Entity>Service.java         ← interface
    │   │       └── <Entity>ServiceImpl.java     ← implementation
    │   └── resources/
    │       └── application.yaml
    └── test/
        └── java/com/me/learning/parent/<pkg>/
```

---

## 2. Scripts at a Glance

| Script | What it does | When to run |
|--------|-------------|-------------|
| `New-SakilaModule.ps1` | Creates folder structure, `pom.xml`, `application.yaml`, Maven wrapper, all 4 config classes, all 5 exception classes, and the Spring Boot `Application` main class | **Once** per new module |
| `Generate-EntityFiles.ps1` | Creates DTO records, MapStruct mapper, JPA repository, service interface, service implementation, and REST controller for **one entity** | **Once per entity** after you have written the `@Entity` class |

> ⚠️ **Only the `@Entity` class itself must be written by hand.**
> Everything else is generated.

---

## 3. Port & Package Registry

Track used ports here to avoid conflicts:

| Module | Package (`-Pkg`) | Port |
|--------|-----------------|------|
| `customer-service` | `customerservice` | `8090` |
| `inventory-service` | `inventoryservice` | `8085` |
| `film-service` *(example)* | `filmservice` | `8091` |
| `rental-service` *(example)* | `rentalservice` | `8092` |
| `staff-service` *(example)* | `staffservice` | `8093` |
| *(your new service)* | | |

---

## 4. Import-Order Rule (Checkstyle)

All files **must** follow this group order with a **blank line between each group**:

```
Group 1  java.*
Group 2  jakarta.*
Group 3  ch.*            (logback)
Group 4  net.*           (logstash)
Group 5  org.slf4j.*
Group 6  io.*            (swagger / openapi)
Group 7  org.apache.*
Group 8  org.assertj.*
Group 9  org.hibernate.*
Group 10 org.jspecify.*
Group 11 org.junit.*
Group 12 org.mapstruct.*
Group 13 org.mockito.*
Group 14 org.springframework.*
Group 15 lombok.*
Group 16 com.*
```

**Example — Controller file imports:**

```java
import java.util.List;                              // Group  1

import jakarta.validation.Valid;                    // Group  2

import io.swagger.v3.oas.annotations.Operation;    // Group  6
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;    // Group 14
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;             // Group 15
import lombok.extern.slf4j.Slf4j;

import com.me.learning.parent.filmservice.dto.*;   // Group 16
import com.me.learning.parent.filmservice.service.*;
```

---

## 5. PHASE 1 — Scaffold the Module

> **Script:** `New-SakilaModule.ps1`  
> **Run:** Once, from the project root.

### Syntax

```powershell
.\New-SakilaModule.ps1 `
    -ServiceName "<service-folder-name>" `
    -Port        <port-number> `
    -Pkg         "<java-subpackage>"
```

### Parameters

| Parameter | Description | Example |
|-----------|-------------|---------|
| `-ServiceName` | Folder name — use kebab-case | `film-service` |
| `-Port` | HTTP server port (pick unused — see §3) | `8091` |
| `-Pkg` | Java sub-package — no hyphens, lowercase | `filmservice` |

### What gets created automatically

| File / Folder | Description |
|---------------|-------------|
| `<service>/pom.xml` | Full Maven POM inheriting from `awsab-parent` |
| `<service>/mvnw`, `mvnw.cmd`, `.mvn/` | Maven wrapper (copied from `customer-service`) |
| `src/main/resources/application.yaml` | Datasource + JPA + Swagger + logging config |
| `…/config/CacheConfig.java` | `@EnableCaching` with placeholder cache names |
| `…/config/LoggingInterceptor.java` | HTTP request/response logging |
| `…/config/OpenAPIConfig.java` | Springdoc OpenAPI bean |
| `…/config/WebMvcConfig.java` | CORS + registers logging interceptor |
| `…/exception/*.java` | All 5 exception classes |
| `…/<PascalName>Application.java` | Spring Boot main class |

### Example

```powershell
# Run from C:\Ramu\GitHub_Repo\sakila-project
.\New-SakilaModule.ps1 -ServiceName "film-service" -Port 8091 -Pkg "filmservice"
```

### After running — update CacheConfig

Open `film-service/src/…/filmservice/config/CacheConfig.java`
and replace the placeholder cache names **after you know your entities**:

```java
// Before (placeholder):
return new ConcurrentMapCacheManager("items", "item");

// After (example for film-service):
return new ConcurrentMapCacheManager(
    "films",      "film",
    "categories", "category",
    "languages",  "language"
);
```

---

## 6. PHASE 2 — Create JPA Entities (Manual)

> **This is the only fully manual step.**

For each database table, create `src/…/<pkg>/entity/<Entity>.java`.
See §11 for the full annotated entity template.

### Entity checklist

- [ ] Class annotated `@Getter @Setter @Entity @Table(name="...")`
- [ ] Implements `Serializable` with `@Serial private static final long serialVersionUID`
- [ ] `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)` on the PK
- [ ] PK type is `Integer` (most tables) or `Short` (tinyint UNSIGNED tables)
- [ ] All `NOT NULL` columns have `@NotNull` + `nullable = false`
- [ ] String columns have `@Size(max = N)` matching the DB length
- [ ] FK columns use `@ManyToOne(fetch = FetchType.LAZY)` + `@JoinColumn`
- [ ] `lastUpdate` field is `Instant` with `@ColumnDefault("CURRENT_TIMESTAMP")`
- [ ] Import order follows §4

---

## 7. PHASE 3 — Generate Entity Boilerplate

> **Script:** `Generate-EntityFiles.ps1`  
> **Run:** Once per entity, **after** you have written the `@Entity` class.

### Syntax

```powershell
.\Generate-EntityFiles.ps1 `
    -ServiceName  "<service-folder-name>" `
    -Pkg          "<java-subpackage>" `
    -Entity       "<PascalCaseEntityName>" `
    -IdType       "<Integer|Short>" `
    -HasNameField <$true|$false>
```

### Parameters

| Parameter | Description | Default | Example |
|-----------|-------------|---------|---------|
| `-ServiceName` | Module folder name | *(required)* | `film-service` |
| `-Pkg` | Java sub-package | *(required)* | `filmservice` |
| `-Entity` | Entity class name in PascalCase | *(required)* | `Film` |
| `-IdType` | Java type of the primary key | `Integer` | `Short` |
| `-HasNameField` | Generate `existsByNameIgnoreCase` duplicate check | `$true` | `$false` |

### What gets created automatically (per entity)

| File | Notes |
|------|-------|
| `dto/<Entity>Request.java` | Record with `@NotBlank` + `@Size` on `name`; add FK fields manually |
| `dto/<Entity>UpdateRequest.java` | Same as Request but for update/patch |
| `dto/<Entity>Response.java` | Plain record: `id`, `name`, `lastUpdate`; add FK fields manually |
| `mapper/<Entity>Mapper.java` | MapStruct interface; add `@Mapping` for FKs manually |
| `repository/<Entity>Repository.java` | `JpaRepository` with `findByName` / `existsByName` helpers |
| `service/<Entity>Service.java` | Full service interface (CRUD + pageable + count + exists) |
| `service/<Entity>ServiceImpl.java` | Full `@Transactional` implementation with cache eviction |
| `controller/<Entity>Controller.java` | REST controller with Swagger `@Operation` on every endpoint |

### Example — simple entity (no FK fields)

```powershell
.\Generate-EntityFiles.ps1 `
    -ServiceName "film-service" `
    -Pkg         "filmservice" `
    -Entity      "Language"
```

### Example — entity with FK, Short PK, no name field

```powershell
.\Generate-EntityFiles.ps1 `
    -ServiceName  "film-service" `
    -Pkg          "filmservice" `
    -Entity       "FilmActor" `
    -IdType       "Short" `
    -HasNameField $false
```

---

## 8. PHASE 4 — Wire Up the Remaining TODO Items

After `Generate-EntityFiles.ps1` runs, the console prints exactly what still needs manual work.
Here is the complete checklist for **each entity**:

### 4a. DTO Records — add FK fields

Open `dto/<Entity>Request.java` and `dto/<Entity>UpdateRequest.java` and add FK fields:

```java
// FK to another entity (Integer PK):
@NotNull(message = "Language ID must not be null")
@Positive(message = "Language ID must be positive")
Integer languageId,

// Optional FK (nullable in DB):
@Positive(message = "Original language ID must be positive")
Integer originalLanguageId,

// Boolean flag:
@NotNull(message = "Active flag must not be null")
Boolean active,

// Monetary amount:
@NotNull(message = "Rental rate must not be null")
@DecimalMin(value = "0.0", inclusive = false, message = "Rental rate must be positive")
@Digits(integer = 4, fraction = 2)
BigDecimal rentalRate,
```

Open `dto/<Entity>Response.java` and add corresponding output fields:

```java
// FK id + resolved name:
Integer languageId,
String  languageName,
```

### 4b. Mapper — add @Mapping for FK fields

Open `mapper/<Entity>Mapper.java` and add annotations (see §13 for rules):

```java
// Request → Entity:
@Mapping(target = "language.id",         source = "languageId")
@Mapping(target = "originalLanguage.id", source = "originalLanguageId")
Film toEntity(FilmRequest request);

// Entity → Response:
@Mapping(source = "language.id",   target = "languageId")
@Mapping(source = "language.name", target = "languageName")
FilmResponse toResponse(Film entity);
```

### 4c. ServiceImpl — add FK validation + field assignments

In `update<Entity>()` fill in field assignments and FK validation:

```java
existing.setTitle(request.title());
existing.setDescription(request.description());

// FK validation before save:
Language lang = languageRepository.findById(request.languageId())
    .orElseThrow(() -> new ResourceNotFoundException("Language", "id", request.languageId()));
existing.setLanguage(lang);
```

Add FK repositories to the constructor (Lombok handles this automatically):

```java
private final FilmRepository repository;
private final FilmMapper mapper;
private final LanguageRepository languageRepository;   // ← add FK repos
```

### 4d. Repository — add domain-specific queries

Open `repository/<Entity>Repository.java` and add relevant derived query methods:

```java
List<Film> findByLanguageId(Integer languageId);
List<Film> findByRentalDurationGreaterThanEqual(Byte duration);
boolean existsByTitleIgnoreCase(String title);
```

### 4e. CacheConfig — update cache names

Open `config/CacheConfig.java` and add the new entity's cache names:

```java
return new ConcurrentMapCacheManager(
    "films",     "film",
    "languages", "language",
    "categories","category"
);
```

---

## 9. PHASE 5 — Verify the Build

```powershell
cd film-service
.\mvnw.cmd compile -q
```

**No output = ✅ success.**

If the build fails, check:

| Error type | Likely cause | Fix |
|------------|-------------|-----|
| `cannot find symbol` on entity field | FK `@Mapping` source path wrong | Check entity field name spelling |
| Checkstyle import order | Groups mixed or blank line missing | Follow §4 exactly |
| `unmapped target property` | New field added to entity but not in mapper | Add `@Mapping` or `@Mapping(target="x", ignore=true)` |
| `Line is longer than 140` | Long log message / annotation | Break into multi-line |
| `@ColumnDefault` not found | Wrong import | Use `org.hibernate.annotations.ColumnDefault` |

---

## 10. Full Worked Example — film-service

This section walks through creating `film-service` from scratch.

### Entities in this service

| Entity | Table | PK type | Has name field |
|--------|-------|---------|---------------|
| `Language` | `language` | `Integer` | ✅ `name` |
| `Film` | `film` | `Integer` | ✅ `title` |
| `Category` | `category` | `Integer` | ✅ `name` |
| `Actor` | `actor` | `Integer` | ❌ (first/last name) |
| `FilmCategory` | `film_category` | `Integer` | ❌ |

---

### Step 1 — Scaffold the module

```powershell
cd C:\Ramu\GitHub_Repo\sakila-project

.\New-SakilaModule.ps1 -ServiceName "film-service" -Port 8091 -Pkg "filmservice"
```

Expected output:
```
========================================
  Scaffolding: film-service
  Port       : 8091
  Package    : com.me.learning.parent.filmservice
========================================

[1/6] Creating directory tree...
[2/6] Copying Maven wrapper...
[3/6] Writing pom.xml...
  [created] pom.xml
[4/6] Writing application.yaml...
  [created] application.yaml
[5/6] Copying exception and config classes...
  [created] ResourceNotFoundException.java
  [created] DuplicateResourceException.java
  [created] InvalidRequestException.java
  [created] ServiceException.java
  [created] GlobalExceptionHandler.java
  [created] LoggingInterceptor.java
  [created] WebMvcConfig.java
  [created] CacheConfig.java
  [created] OpenAPIConfig.java
[6/6] Writing FilmServiceApplication.java...
  [created] FilmServiceApplication.java

========================================
  Module scaffold complete!
========================================
```

---

### Step 2 — Write entities (manual)

Create `film-service/src/…/filmservice/entity/Language.java`:

```java
package com.me.learning.parent.filmservice.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "language")
public class Language implements Serializable {

    @Serial
    private static final long serialVersionUID = -1234567890123456789L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "last_update", nullable = false)
    private Instant lastUpdate;
}
```

Repeat for `Film`, `Category`, `Actor`, `FilmCategory`.

---

### Step 3 — Generate boilerplate for each entity

```powershell
# Language — simple, has name field
.\Generate-EntityFiles.ps1 -ServiceName "film-service" -Pkg "filmservice" -Entity "Language"

# Film — has name field (title), Integer PK
.\Generate-EntityFiles.ps1 -ServiceName "film-service" -Pkg "filmservice" -Entity "Film"

# Category — simple, has name field
.\Generate-EntityFiles.ps1 -ServiceName "film-service" -Pkg "filmservice" -Entity "Category"

# Actor — no single "name" field
.\Generate-EntityFiles.ps1 -ServiceName "film-service" -Pkg "filmservice" -Entity "Actor" -HasNameField $false

# FilmCategory — junction table, no name field
.\Generate-EntityFiles.ps1 -ServiceName "film-service" -Pkg "filmservice" -Entity "FilmCategory" -HasNameField $false
```

---

### Step 4 — Apply TODOs (FK entities)

For `Film` (has FK to `Language`):
1. Add `languageId` to `FilmRequest`, `FilmUpdateRequest`, `FilmResponse`
2. Add `@Mapping(target = "language.id", source = "languageId")` to `FilmMapper.toEntity()`
3. Add `@Mapping(source = "language.id", target = "languageId")` to `FilmMapper.toResponse()`
4. In `FilmServiceImpl.updateFilm()`, validate `languageId` against `LanguageRepository` and call `existing.setLanguage(lang)`

---

### Step 5 — Update CacheConfig

```java
return new ConcurrentMapCacheManager(
    "films",      "film",
    "languages",  "language",
    "categories", "category",
    "actors",     "actor"
);
```

---

### Step 6 — Verify

```powershell
cd film-service
.\mvnw.cmd compile -q
# No output = success ✅
```

---

## 11. JPA Entity Checklist

Full annotated template — copy and adapt per entity:

```java
package com.me.learning.parent.<pkg>.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "my_table")                            // ← exact DB table name
public class MyEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // ← generate unique long

    // ── Primary Key ──────────────────────────────────────────────────────────
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_entity_id", nullable = false)
    private Integer id;                              // use Short for tinyint UNSIGNED

    // ── String (NOT NULL) ────────────────────────────────────────────────────
    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // ── String (nullable) ────────────────────────────────────────────────────
    @Size(max = 255)
    @Column(name = "description")
    private String description;

    // ── FK (NOT NULL) ────────────────────────────────────────────────────────
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_id", nullable = false)
    private ParentEntity parent;

    // ── FK (nullable) ────────────────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optional_parent_id")
    private OtherEntity optionalParent;

    // ── Boolean ──────────────────────────────────────────────────────────────
    @NotNull
    @ColumnDefault("1")
    @Column(name = "active", nullable = false)
    private Boolean active;

    // ── Audit timestamp ──────────────────────────────────────────────────────
    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "last_update", nullable = false)
    private Instant lastUpdate;
}
```

---

## 12. DTO Validation Reference

All DTOs are **Java records** (no classes). Use these annotations per field type:

| Field type | Required | Optional / Nullable |
|------------|----------|---------------------|
| `String` (name/title) | `@NotBlank` + `@Size(max=N)` | — |
| `String` (optional text) | — | `@Size(max=N)` |
| `String` (email) | `@Email` + `@Size(max=50)` | — |
| `String` (phone) | `@Pattern(regexp="^[+\\d\\s\\-()]*$")` + `@Size` | — |
| `String` (postal code) | `@Pattern(regexp="^[A-Z0-9\\- ]{3,10}$")` | — |
| `Integer` FK id | `@NotNull` + `@Positive` | `@Positive` only |
| `Short` FK id | `@NotNull` + `@Positive` | `@Positive` only |
| `Boolean` flag | `@NotNull` | — |
| `BigDecimal` amount | `@NotNull` + `@DecimalMin("0.0")` + `@Digits(integer=N, fraction=2)` | — |
| `Instant` timestamp | `@NotNull` | — |
| `Byte` / `Short` counter | `@NotNull` + `@Min(0)` | — |

---

## 13. MapStruct FK-Mapping Reference

### Direction: Request → Entity (toEntity)

```java
@Mapping(target = "parent.id",    source = "parentId")
@Mapping(target = "optPar.id",    source = "optionalParentId")
MyEntity toEntity(MyEntityRequest request);
```

### Direction: Entity → Response (toResponse)

```java
@Mapping(source = "parent.id",    target = "parentId")
@Mapping(source = "parent.name",  target = "parentName")
@Mapping(source = "optPar.id",    target = "optionalParentId")
MyEntityResponse toResponse(MyEntity entity);
```

### Direction: UpdateRequest → Entity patch (updateEntity)

```java
@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Mapping(target = "parent.id", source = "parentId")
void updateEntity(MyEntityUpdateRequest request, @MappingTarget MyEntity entity);
```

> **Rule:** `source` and `target` paths are **swapped** between read and write directions.

---

## 14. Naming Conventions Cheat Sheet

| Concept | Convention | Example |
|---------|-----------|---------|
| Module folder | `kebab-case` | `film-service` |
| Java package (`-Pkg`) | `lowercase`, no hyphens | `filmservice` |
| Spring Boot main class | `PascalCase` + `Application` | `FilmServiceApplication` |
| Entity class | `PascalCase` | `Film`, `FilmActor` |
| DTO — create | `<Entity>Request` | `FilmRequest` |
| DTO — update | `<Entity>UpdateRequest` | `FilmUpdateRequest` |
| DTO — read | `<Entity>Response` | `FilmResponse` |
| MapStruct mapper | `<Entity>Mapper` | `FilmMapper` |
| JPA repository | `<Entity>Repository` | `FilmRepository` |
| Service interface | `<Entity>Service` | `FilmService` |
| Service impl | `<Entity>ServiceImpl` | `FilmServiceImpl` |
| REST controller | `<Entity>Controller` | `FilmController` |
| REST base path | `/api/v1/<entities>` (plural, kebab) | `/api/v1/films` |
| Cache names | `"<entities>"` + `"<entity>"` | `"films"`, `"film"` |
| Server port | Pick next unused (see §3) | `8091`, `8092`, … |

---

## 15. Troubleshooting

### `Script cannot be loaded because running scripts is disabled`

```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```

### `Cannot bind argument to parameter 'Path' because it is null`

The `-ServiceName` module folder doesn't exist yet.
Run `New-SakilaModule.ps1` first before `Generate-EntityFiles.ps1`.

### Checkstyle: `Import ordering violation`

Open the failing `.java` file.
Reorder imports according to the 16-group rule in §4.
Each group must be separated by one blank line.

### `Cannot find symbol: class <Entity>`

The `@Entity` class hasn't been created yet, or the package is wrong.
Verify the entity is in `entity/<Entity>.java` with the correct package declaration.

### `MapStruct: Unmapped target property`

Add `@Mapping(target = "fieldName", ignore = true)` in the mapper,
or add the proper `@Mapping(target=…, source=…)` annotation.

### `Port already in use`

Check §3, pick the next available port, and update `application.yaml`.

---

*Runbook version: 2.0 — Updated 2026-04-20 — Sakila Microservices Project*
