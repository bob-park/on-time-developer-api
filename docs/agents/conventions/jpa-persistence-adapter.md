---
title: JPA Persistence Adapter Conventions
scope: src/main/java/**/*.java
applies_to: adding a JPA repository or QueryDSL query
related:
  - ./jpa-entity.md
  - ./naming.md
  - ../libs/jpa-querydsl-sorts.md
  - ./web-api.md
---

# JPA Persistence Adapter Conventions

> Repository layering, QueryDSL idioms, paging. Read when adding a JPA repository or QueryDSL query.

Rules for `{module}.adapter.out.persistence.jpa` (JPA repositories + QueryDSL implementations) and the outbound ports they implement in `{module}.application.required`. Applies to all new persistence code. The template ships no persistence adapters yet; these conventions are the target for the first aggregate added.

## Repository Layering

Each aggregate `{Name}` is persisted through **four collaborators**:

| Layer | Type | Package | Stereotype |
| ----- | ---- | ------- | ---------- |
| Outbound port | `{Name}Repository` (interface; role-noun per [Naming](naming.md)) | `application.required` | — |
| Spring Data repo | `{Name}JpaRepository extends JpaRepository<{Name}, Long>, {Name}JpaQueryRepository` | `adapter.out.persistence.jpa` | — |
| Port adapter | `{Name}JpaRepositoryAdapter implements {Name}Repository` | `adapter.out.persistence.jpa` | `@RequiredArgsConstructor` + `@Repository` |
| QueryDSL fragment | `{Name}JpaQueryRepository` (iface) + `{Name}JpaQueryRepositoryImpl` | `…jpa.query` + `…jpa.query.impl` | `@RequiredArgsConstructor` |

Rules:

- The `…Impl` suffix on the QueryDSL fragment is **mandatory** — it is Spring Data's custom-fragment naming contract. `{Name}JpaRepository` inherits `{Name}JpaQueryRepository`, and Spring Data wires `{Name}JpaQueryRepositoryImpl` automatically by name.
- The application layer depends **only** on the `{Name}Repository` port in `required`. It never references `{Name}Jpa*` types.
- `{Name}JpaRepositoryAdapter` is a **thin delegate**: each method forwards to the injected `{Name}JpaRepository`. No business logic.
- The **QueryDSL fragment tier is optional**. An aggregate that needs only CRUD and derived-name lookups drops the `…query` and `…query.impl` packages entirely; then `{Name}JpaRepository extends JpaRepository<{Name}, Long>` with no fragment, and the adapter delegates to Spring Data's inherited methods. Introduce the fragment the moment a query needs dynamic filtering, joins, or paging.
- **Simple static lookups** may be declared as **derived query methods** on `{Name}JpaRepository` itself (e.g. `Optional<{Name}> findByType({Name}Type type)`). Reserve the QueryDSL fragment for dynamic or composite queries — do not mix ad-hoc `@Query` strings into the Spring Data interface.

```java
// codes/application/required/CodeRepository.java
public interface CodeRepository {

    Code save(Code code);

    List<Code> search(CodeQueryCondition condition);

    Optional<Code> findCode(Long id);
}
```

Port method naming:

- The **single-entity detail lookup by primary id** is named **`find{EntityName}(Long id)`** and
  returns **`Optional<{Entity}>`** — `Code` → `Optional<Code> findCode(Long id)`. Compound entity
  names use the **full simple class name**: `SessionChair` → `findSessionChair`,
  `PresentationComment` → `findPresentationComment`.
- **Filtered collection lookups** are named **`search(...)`**: `search({Name}QueryCondition)`, or
  `search({Name}QueryCondition, Pageable)` when paged.
- **Lookups by other criteria** get descriptive `findBy…` / `findAllBy…` names (e.g.
  `findAllByStatus(...)`, `findActiveByParentIdAndName(...)`). `find{EntityName}` and `search` are
  reserved for the id-detail and filtered-collection cases.
- **Boolean existence checks** are named `exist…(...)` (e.g. `existName(String name)`).

Search inputs — the filter object a `search(...)` query accepts — are modelled as an immutable
`{Name}QueryCondition` record in `{module}.application.required.model`, next to the other
outbound-port request models. (The provided-port counterpart is `{Name}QueryCriteria` in
`application.provided.model`; the `QueryService` maps one to the other — see
[Application Service](application-service.md).) The repository port and the QueryDSL fragment both
take it by value; the fragment turns it into predicates in `mappingCondition(...)` (see
[QueryDSL Idioms](#querydsl-idioms)).

```java
// codes/adapter/out/persistence/jpa/CodeJpaRepository.java
public interface CodeJpaRepository
    extends JpaRepository<Code, Long>, CodeJpaQueryRepository {
}
```

```java
// codes/adapter/out/persistence/jpa/CodeJpaRepositoryAdapter.java
@RequiredArgsConstructor
@Repository
public class CodeJpaRepositoryAdapter implements CodeRepository {

    private final CodeJpaRepository repository;

    @Override
    public Code save(Code code) {
        return repository.save(code);
    }

    @Override
    public List<Code> search(CodeQueryCondition condition) {
        return repository.search(condition);
    }

    @Override
    public Optional<Code> findCode(Long id) {
        return repository.findCode(id);
    }
}
```

## QueryDSL Idioms

`{Name}JpaQueryRepositoryImpl` holds all QueryDSL queries.

- `@RequiredArgsConstructor`; inject `JPAQueryFactory` (the `queryFactory` bean from `config.jpa.JpaConfiguration`, see [Config Dependency](#config-dependency)).
- Static-import the Q-type: `import static com.malgn.domain.codes.QCode.*;`.
- For self-joins, declare a named alias: `private static final QCode parent = new QCode("parent");`.
- Build dynamic filters in a `private Predicate mappingCondition(...)` using a `BooleanBuilder`, composed from `private BooleanExpression` helpers that **return `null` to skip** the predicate (QueryDSL ignores null predicates — no manual `if` chains in the query body).
- Wrap single-row lookups in `Optional.ofNullable(query.…fetchOne())`.
- Existence checks: `query.selectOne().from(...).where(...).fetchFirst() != null`.

```java
// codes/adapter/out/persistence/jpa/query/impl/CodeJpaQueryRepositoryImpl.java
@RequiredArgsConstructor
public class CodeJpaQueryRepositoryImpl implements CodeJpaQueryRepository {

    private static final QCode parent = new QCode("parent");

    private final JPAQueryFactory query;

    @Override
    public List<Code> search(CodeQueryCondition condition) {
        return query.selectFrom(code)
            .leftJoin(code.parent, parent).fetchJoin()
            .where(mappingCondition(condition))
            .orderBy(code.createdDate.asc())
            .fetch();
    }

    @Override
    public Optional<Code> findCode(Long id) {
        return Optional.ofNullable(
            query.selectFrom(code)
                .leftJoin(code.parent, parent).fetchJoin()
                .where(code.id.eq(id))
                .fetchOne());
    }

    private Predicate mappingCondition(CodeQueryCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqDeleted(false));
        builder.and(eqParentId(condition.parentId()));
        builder.and(containsName(condition.name()));

        return builder;
    }

    private BooleanExpression eqDeleted(Boolean deleted) {
        return deleted == null ? null : code.deleted.eq(deleted);
    }

    private BooleanExpression eqParentId(Long parentId) {
        return parentId != null ? code.parent.id.eq(parentId) : null;
    }

    private BooleanExpression containsName(String name) {
        return StringUtils.isNotBlank(name) ? code.name.containsIgnoreCase(name) : null;
    }
}
```

## Paging & Sorting

- Paged query methods declare `org.springframework.data.domain.Page<T>` and accept `org.springframework.data.domain.Pageable` (the controller boundary wraps this in `PagedModel<T>` per [Web API Conventions](web-api.md)).
- Build the page with a **separate count query** and `PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne)`.
- Ordering **always** goes through a `private OrderSpecifier<?>[] sort(Pageable)` helper. See [QueryDSL Pageable Sorting](../libs/jpa-querydsl-sorts.md) for the helper and its rules.

```java
@RequiredArgsConstructor
public class ConferenceJpaQueryRepositoryImpl implements ConferenceJpaQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Page<Conference> search(ConferenceQueryCondition condition, Pageable pageable) {

        List<Conference> content =
            query.selectFrom(conference)
                .where(mappingCondition(condition))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sort(pageable))
                .fetch();

        JPAQuery<Long> countQuery =
            query.select(conference.id.count())
                .from(conference)
                .where(mappingCondition(condition));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // sort(Pageable) — see ../libs/jpa-querydsl-sorts.md
}
```

## Config Dependency

The QueryDSL fragments depend on the `JPAQueryFactory` bean and `@EnableJpaAuditing`, both declared in `config.jpa.JpaConfiguration`. `@EnableJpaAuditing` (with the `AuditorAware<String>` bean) is what populates the `BaseEntity` audit fields ([JPA Entity Conventions](jpa-entity.md)). Both already exist in the template — new persistence code consumes them, it does not redeclare them.
