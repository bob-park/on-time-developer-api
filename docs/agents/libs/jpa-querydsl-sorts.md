---
title: QueryDSL Pageable Sorting
scope: src/main/java/**/*.java
applies_to: a QueryDSL query needs `Pageable`-driven ordering
related:
  - ../conventions/jpa-persistence-adapter.md
---

# QueryDSL Pageable Sorting

> Applying `Pageable` ordering in QueryDSL via `QueryRepositoryUtils.sort` + `QueryDslPath` (malgn starter). Read when a QueryDSL query needs `Pageable`-driven ordering.

Ordering in a paged QueryDSL query **always** goes through a `private OrderSpecifier<?>[] sort(Pageable)` helper delegating to `QueryRepositoryUtils.sort(...)` with an explicit `QueryDslPath` whitelist. **Never** hand-roll `OrderSpecifier`s from `pageable.getSort()` — the whitelist is what prevents sorting by arbitrary/unsafe columns.

Imports: `com.malgn.starter.common.querydsl.utils.QueryRepositoryUtils`, `com.malgn.starter.common.querydsl.model.QueryDslPath`.

```java
/*
 * Order — always via QueryRepositoryUtils.sort + a QueryDslPath whitelist.
 */
private OrderSpecifier<?>[] sort(Pageable pageable) {
    return QueryRepositoryUtils.sort(
        pageable,
        List.of(
            new QueryDslPath<>("title", conference.title),
            new QueryDslPath<>("status", conference.status),
            new QueryDslPath<>("startDate", conference.startDate),
            new QueryDslPath<>("createdDate", conference.createdDate)));
}
```

Each `QueryDslPath<>("<sortKey>", <qPath>)` maps an API-facing sort key to a QueryDSL path; only listed keys are sortable. The helper is called from the paged query's `.orderBy(sort(pageable))` — see [JPA Persistence Adapter Conventions](../conventions/jpa-persistence-adapter.md).
