---
title: Application Service Conventions
scope: src/main/java/**/*.java
applies_to: adding a use-case service, a controller dependency, or an application-layer model
related:
  - ../architecture.md
  - ./web-api.md
  - ./jpa-persistence-adapter.md
  - ./naming.md
---

# Application Service Conventions

> Command/Query service split, transaction boundaries, port injection, and application-layer result & search-condition records. Read when adding a use-case service, a controller dependency, or an application-layer model.

Use cases live in `{module}.application.service` and implement the inbound ports declared in
`{module}.application.provided`. Services are split by read/write responsibility.

## Provided Port Naming

One role interface per operation kind, named after the aggregate:

| Operation | Provided port | Implemented by |
| --------- | ------------- | -------------- |
| 조회 (read) | `{Name}Query` | `{Name}QueryService` |
| 생성 (create) | `{Name}Register` | `{Name}CommandService` |
| 수정/삭제 (update & delete) | `{Name}Editor` | `{Name}CommandService` |

- `{Name}Editor` covers **both** update and delete (and state restores) — there is no separate
  `Deleter` port.
- A focused special-purpose mutation port may be named `{Name}Manager` (e.g. a password-change port
  kept separate from the aggregate's `Editor`); its implementation is still a `…CommandService`.
- Sub-aggregate operations get their own port set (`{Name}{Sub}Query`, `{Name}{Sub}Editor`, …)
  implemented by `{Name}{Sub}QueryService` / `{Name}{Sub}CommandService`.

`{Name}Query` method vocabulary: `get{Name}(Long id)` (detail; throws when absent),
`getBy…(…)` for alternate-key detail lookups, `search({Name}QueryCriteria[, Pageable])` for filtered
collections, and `exist…(…)` for boolean existence checks.

## Command / Query Split

- `{Name}CommandService` implements the write ports (`{Name}Register`, `{Name}Editor`) and is annotated `@Transactional`.
- `{Name}QueryService` implements the read port (`{Name}Query`) and is annotated `@Transactional(readOnly = true)`.
- **Both annotations are Spring's `org.springframework.transaction.annotation.Transactional`.** (Existing downstream code sometimes places `jakarta.transaction.Transactional` on command services; new code standardizes on the Spring annotation for both so the read/write distinction is expressed only via `readOnly`.)
- A bounded context may add a focused extra service for a distinct operation (e.g. a `BoothCheckInCommandService` separate from `BoothCommandService`) rather than overloading one class.

```java
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CodeCommandService implements CodeRegister, CodeEditor {

    private final CodeRepository codeRepository;

    @Override
    public CodeResult register(CodeRegisterCommand command) {
        // verify references, enforce invariants, mutate, save, then map to a Result
        // ...
    }
}
```

```java
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CodeQueryService implements CodeQuery {

    private final CodeRepository codeRepository;

    @Override
    public List<CodeResult> search(CodeQueryCriteria queryCriteria) {
        CodeQueryCondition queryCondition =
            CodeQueryCondition.builder()
                .parentId(queryCriteria.parentId())
                .name(queryCriteria.name())
                .build();

        return codeRepository.search(queryCondition).stream()
            .map(CodeResult::from)
            .toList();
    }
}
```

## Controllers Inject Ports, Not Services

Controllers depend on the inbound port interfaces (`CodeQuery`, `CodeRegister`, `CodeEditor`), never the concrete `*Service` classes. This keeps the web adapter behind the hexagonal boundary — the service implementation can be split or replaced without touching the controller.

```java
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "codes", version = "1")
public class CodeApiV1 {

    private final CodeRegister register;
    private final CodeQuery query;
    private final CodeEditor editor;
    // ...
}
```

## Result Records

Application results are returned as `@Builder` records in `{module}.application.provided.model`, with a static `from(entity)` factory. When nested mapping depth varies, add a `from(entity, boolean detail)` overload so callers choose whether nested associations are mapped — this guards against N+1 / `LazyInitializationException` on the default (shallow) path.

```java
@Builder
public record CodeResult(Long id,
                         CodeResult parent,
                         String name,
                         String displayName,
                         String description,
                         LocalDateTime createdDate,
                         String createdBy,
                         LocalDateTime lastModifiedDate,
                         String lastModifiedBy) {

    public static CodeResult from(Code code) {
        return from(code, false);
    }

    public static CodeResult from(Code code, boolean detail) {
        return CodeResult.builder()
            .id(code.getId())
            .parent(detail ? fromParent(code.getParent()) : null)
            .name(code.getName())
            .displayName(code.getDisplayName())
            .description(code.getDescription())
            .createdDate(code.getCreatedDate())
            .createdBy(code.getCreatedBy())
            .lastModifiedDate(code.getLastModifiedDate())
            .lastModifiedBy(code.getLastModifiedBy())
            .build();
    }

    private static CodeResult fromParent(Code parent) {
        if (parent == null) {
            return null;
        }
        return CodeResult.builder()
            .id(parent.getId())
            .name(parent.getName())
            .displayName(parent.getDisplayName())
            .description(parent.getDescription())
            .build();
    }
}
```

## Per-Port Model Records

Each port surface carries its own DTO records, named by role:

| Record | Package | Role |
| ------ | ------- | ---- |
| `{Name}Result` | `application.provided.model` | Port return value, `from(entity)` factory |
| `{Name}{Action}Command` | `application.provided.model` | Mutation input for `Register`/`Editor` ports (`CodeRegisterCommand`, `CodeUpdateCommand`, …) |
| `{Name}QueryCriteria` | `application.provided.model` | Filter input the `{Name}Query` port accepts |
| `{Name}QueryCondition` | `application.required.model` | Filter input the `{Name}Repository` port accepts |

`{Name}QueryCriteria` (provided) and `{Name}QueryCondition` (required) are **separate records even when
their fields coincide**: the provided model is the module's public contract (a named interface other
modules may call), while the required model faces persistence and stays module-internal. The
`QueryService` maps one to the other, so neither adapter layer sees the other side's type.

Both are `@Builder` records holding only nullable filter fields with **no defaults**, so a caller
passes only the fields it filters on (the QueryDSL predicate builder skips null predicates — see
[jpa-persistence-adapter](jpa-persistence-adapter.md)). Use `@Builder(toBuilder = true)` when a
condition needs to be rebuilt from an existing one.

```java
// application/provided/model/CodeQueryCriteria.java
@Builder(toBuilder = true)
public record CodeQueryCriteria(Long parentId,
                                 String name) {
}

// application/required/model/CodeQueryCondition.java
@Builder(toBuilder = true)
public record CodeQueryCondition(Long parentId,
                                 String name) {
}
```
