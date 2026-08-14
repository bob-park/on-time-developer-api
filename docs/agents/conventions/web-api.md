---
title: Web API Conventions
scope: src/main/java/**/*.java
applies_to: writing a REST controller or request/response DTO
related:
  - ../structure-dir.md
  - ./builder-pattern.md
  - ./naming.md
---

# Web API Conventions

> REST controller naming/versioning, DTOs, String IDs, PagedModel. Read when writing a REST controller or request/response DTO.

Rules for `{module}.adapter.in.web.api.v{version}` — REST controllers and their request/response DTOs. All new web APIs are versioned by default.

## Controller Naming & API Versioning

- Class name suffix is `…ApiV{version}` (e.g., `CodeApiV1`, `AssetApiV1`), annotated with `@RestController`. The `V{version}` suffix must match the value passed to `@RequestMapping(version = "…")` so the class name, package path, and routing version stay in lockstep.
- Version the endpoint via `@RequestMapping`'s `version` attribute, **not** via URL path prefix. The path itself is written without a leading slash.
- The Java package encodes the version only: `{module}.adapter.in.web.api.v1`. All of a version's
  controllers live in that **single package** — there are no per-resource sub-packages — so the
  file-system layout remains aligned with [Directory Structure](../structure-dir.md).

```java
@RequiredArgsConstructor
@RequestMapping(path = "codes", version = "1")
@RestController
public class CodeApiV1 {
    // ...
}
```

## Endpoint Path Naming

The value passed to `@RequestMapping(path = "…")` follows these rules. The version is **never** in
the path (it lives in the `version` attribute), and the path has **no leading slash**.

- **Plural domain noun, lowercase, kebab-case.** The path is the plural form of the domain name:
  `Code` → `codes`. Pluralize by **English grammar**, not a naive `-s`: `Category` → `categories`
  (`-y` → `-ies`), irregular nouns per grammar. Multi-word names are kebab-cased and lowercased
  (`AccessLog` → `access-logs`).
- **Nested resources** use `parent-plural/{parentId:\d+}/child-plural`. The last segment is the
  plural of the **trailing noun** of the domain name; the parent segment is the plural of the
  parent domain. The parent-id path variable **always** carries the `:\d+` numeric constraint.
  - `RaffleEntry` → parent `Raffle` (`raffles`) + trailing `Entry` (`entries`)
    ⇒ `raffles/{raffleId:\d+}/entries`.
  - **No extra package for the nested resource.** The nesting lives in the URL and the class name
    only: class `RaffleEntryApiV1` sits in the same `adapter.in.web.api.v1` package as
    `RaffleApiV1`, and its DTOs share the version's single `dto` package. One controller class per
    nested resource (`UserPositionApiV1`, `UserPasswordApiV1`, … style), not per-resource
    sub-packages.
- **State-transition actions** (not plain CRUD) use a verb sub-path on the resource id:
  `POST {id:\d+}/{verb}`, where `{verb}` is a single lowercase English word
  (`invite`, `accept`, `decline`, `cancel`, `submit`, `award`).

```java
// flat resource — Code → codes
@RequestMapping(path = "codes", version = "1")

// nested resource — RaffleEntry → raffles/{raffleId:\d+}/entries
// package com.malgn.raffles.adapter.in.web.api.v1; class RaffleEntryApiV1 (same package as RaffleApiV1)
@RequestMapping(path = "raffles/{raffleId:\\d+}/entries", version = "1")

// state-transition action
@PostMapping(path = "{id:\\d+}/cancel")
public RaffleEntryResponseV1 cancel(@PathVariable long id) {
    return from(editor.cancel(id));
}
```

## Request / Response DTOs

- DTOs live in the version's single shared `dto` sub-package: `{module}.adapter.in.web.api.v1.dto`.
  All controllers of that version (including nested-resource controllers) share it.
- DTO class names must carry the version suffix matching `@RequestMapping(version = "…")`:
  - Request: `{Resource}{Action}RequestV1` — qualify the request by the action it carries
    (`CodeRegisterRequestV1`, `CodeUpdateRequestV1`, `CodeSearchRequestV1`). Write requests
    (register/update) declare their Jakarta Bean Validation annotations inline; search requests
    hold nullable filter fields and carry no validation.
  - Response: `{Resource}ResponseV1` for the primary resource. Boolean existence-check endpoints
    return a dedicated `{Resource}{Field}ExistResponseV1` (e.g. `CodeNameExistResponseV1`).
- DTOs are immutable `record` types with `@Builder` ([Builder Pattern](builder-pattern.md)).
- Map application-layer results to response DTOs via a static `from(...)` factory method on the response DTO itself.

Example package layout:

```text
com.malgn.codes.adapter.in.web.api.v1
├── CodeApiV1                # @RestController + @RequestMapping(path = "codes", version = "1")
├── CodeSummaryApiV1         # sibling resource — same package
├── CodeEntryApiV1           # nested resource (codes/{codeId:\d+}/entries) — same package
└── dto                      # single dto package shared by every controller of the version
    ├── CodeRegisterRequestV1
    ├── CodeSearchRequestV1
    ├── CodeResponseV1
    ├── CodeSummaryResponseV1
    └── CodeEntryResponseV1
```

## Controller Method Conventions

- **Numeric path variables are constrained at the mapping.** ID path variables use the `{id:\d+}` regex so only digits match: `@GetMapping(path = "{id:\\d+}")`, `@DeleteMapping(path = "{id:\\d+}")`, etc.
- **Response status is explicit on mutations.** `@ResponseStatus(HttpStatus.CREATED)` on `POST`/register; `@ResponseStatus(HttpStatus.NO_CONTENT)` on `DELETE`. A `DELETE` handler still returns the deleted entity's response DTO (not `void`). `GET`/`PUT` return 200 implicitly.
- **Unpaged collections return a bare `List<…ResponseV1>`** — there is no custom response-envelope wrapper. (Paged endpoints use `PagedModel<T>`; see below.)
- **Static-import the response DTO's `from(...)` factory** so the controller calls `from(result)` directly. The static import sits in its own group per [import-order](import-order.md).

```java
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "codes", version = "1")
public class CodeApiV1 {

    private final CodeRegister register;
    private final CodeQuery query;
    private final CodeEditor editor;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public CodeResponseV1 register(@RequestBody @Valid CodeRegisterRequestV1 request) {
        CodeResult result = register.register(/* map request → CodeRegisterRequest */);
        return from(result);
    }

    @GetMapping(path = "")
    public List<CodeResponseV1> getAll(CodeSearchRequestV1 request) {
        List<CodeResult> results = query.search(/* map request → CodeQueryCriteria */);
        return results.stream()
            .map(CodeResponseV1::from)
            .toList();
    }

    @GetMapping(path = "{id:\\d+}")
    public CodeResponseV1 getCode(@PathVariable long id) {
        return from(query.getCode(id));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "{id:\\d+}")
    public CodeResponseV1 removeCode(@PathVariable long id) {
        return from(editor.deleteCode(id));
    }
}
```

The `from(result)` calls above rely on `import static com.malgn.codes.adapter.in.web.api.v1.dto.CodeResponseV1.*;`.

## ID Fields Must Be Serialized as String

All identifier values exposed by a response DTO — primary keys **and** Snowflake-based foreign-key references — must be declared as `String`, not `Long`.

Reason: identifiers are generated by Snowflake, which routinely produces values exceeding 16 digits. JavaScript's `Number` (IEEE-754 double) loses precision above 2^53 (≈16 digits), so transmitting these IDs as JSON numbers corrupts them on the browser side.

Rules:

- Declare the DTO field as `String`. The contract is explicit at the type level and visible in OpenAPI specs.
- Convert with `String.valueOf(...)` inside the DTO's `from(...)` factory. Do **not** rely on Jackson serializer annotations.
- Applies to primary IDs (`id`) **and** foreign-key reference IDs (`userId`, `parent.id`, …).
- Non-identifier `Long` values (amounts, counts) keep their numeric type.

Example:

```java
@Builder
public record CodeResponseV1(String id,
                             @Nullable CodeResponseV1 parent,
                             String name,
                             String displayName,
                             String description,
                             LocalDateTime createdDate,
                             String createdBy,
                             LocalDateTime lastModifiedDate,
                             String lastModifiedBy) {

    public static CodeResponseV1 from(CodeResult result) {
        return CodeResponseV1.builder()
            .id(String.valueOf(result.id()))
            .parent(result.parent() != null ? from(result.parent()) : null)
            .name(result.name())
            .displayName(result.displayName())
            .description(result.description())
            .createdDate(result.createdDate())
            .createdBy(result.createdBy())
            .lastModifiedDate(result.lastModifiedDate())
            .lastModifiedBy(result.lastModifiedBy())
            .build();
    }
}
```

## FK References in Models & DTOs

How a foreign-key reference surfaces in an application-layer `*Result` and an adapter-layer
`*ResponseV1` depends on **whether the referenced aggregate's domain lives in the current module**.

- **FK target is in the current module's domain layer → nest the full related object.** The
  `*Result` carries the related `*Result`, and the `*ResponseV1` carries the related
  `*ResponseV1`, each mapped through its own nested `from(...)`. Optional/nullable nested FKs are
  annotated `@Nullable` per [Null & Validation](null-validation.md).
- **FK target is NOT in the current module's domain (cross-module reference) → carry only the id.**
  The module knows only the id, so:
  - The `*Result` field stays **`Long`** (e.g. `Long registrationId`).
  - The `*ResponseV1` field is **`String`** (e.g. `String registrationId`), converted with
    `String.valueOf(...)` in `from(...)`, consistent with
    [ID Fields Must Be Serialized as String](#id-fields-must-be-serialized-as-string).

Example — `Raffle` is in this module's domain (nested), `registration` is owned by another module
(id only):

```java
// Application Layer Model
@Builder
public record RaffleEntryResult(Long id,
                                RaffleResult raffle,        // in-module FK → full nested Result
                                Long registrationId,        // cross-module FK → id only (Long)
                                ...) {

    public static RaffleEntryResult from(RaffleEntry entry) {
        return RaffleEntryResult.builder()
            .id(entry.getId())
            .raffle(RaffleResult.from(entry.getRaffle()))
            .registrationId(entry.getRegistrationId())
            .build();
    }
}

// Adapter Layer DTO — same structure; ids become String
@Builder
public record RaffleEntryResponseV1(String id,
                                    RaffleResponseV1 raffle,   // in-module FK → full nested Response
                                    String registrationId,     // cross-module FK → id only (String)
                                    ...) {

    public static RaffleEntryResponseV1 from(RaffleEntryResult result) {
        return RaffleEntryResponseV1.builder()
            .id(String.valueOf(result.id()))
            .raffle(RaffleResponseV1.from(result.raffle()))
            .registrationId(String.valueOf(result.registrationId()))
            .build();
    }
}
```

## Paged Responses with PagedModel

REST endpoint methods that return a paged result must declare the return type as `org.springframework.data.web.PagedModel<T>`, not `org.springframework.data.domain.Page<T>`.

Reason: `PagedModel` is the canonical wire-format representation. Although `WebMvcConfiguration` enables `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` (which already serializes `Page` as a `PagedModel`-shaped JSON), making the return type explicit (a) keeps the method signature consistent with the response shape, (b) produces correct OpenAPI schemas, and (c) avoids leaking the `Page` abstraction into the public API contract.

Wrap the application-layer result at the controller boundary:

```java
@GetMapping
public PagedModel<CodeResponseV1> getCodes(CodeSearchRequestV1 request, Pageable pageable) {
    Page<CodeResponseV1> page = query.search(/* map request → CodeQueryCriteria */, pageable)
        .map(CodeResponseV1::from);
    return new PagedModel<>(page);
}
```
