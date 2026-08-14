---
title: Naming
scope: src/main/java/**/*.java
applies_to: naming or renaming any type
related:
  - ./web-api.md
  - ./jpa-persistence-adapter.md
---

# Naming

> Package/class/port/adapter/controller/DTO naming. Read when naming or renaming any type.

- Package: lowercase. Existing modules use plural collective nouns (e.g., `assets`, `users`); follow the same form when adding sibling packages.
- Class: PascalCase.
- Inbound ports (`{module}.application.provided`): one role interface per operation kind —
  `{Name}Query` (reads), `{Name}Register` (create), `{Name}Editor` (update/delete); see
  [Application Service](application-service.md).
- Inbound-port models (`{module}.application.provided.model`): `{Name}Result` (return value),
  `{Name}{Action}Command` (mutation input), and `{Name}QueryCriteria` — the filter input a
  `{Name}Query` port accepts.
- Outbound ports (`{module}.application.required`): role nouns (`{Name}Repository`,
  `StorageManager`).
- Outbound-port models (`{module}.application.required.model`): `{Name}QueryCondition` — the filter
  input the read methods of `{Name}Repository` (`search(...)`) accept. It stays a **separate record**
  from the provided-side `{Name}QueryCriteria` even when the fields coincide; the `QueryService`
  maps one to the other (see [Application Service](application-service.md)).
- Adapter classes: prefix with the technology (`AwsS3StorageManager`) — **except** JPA persistence
  types, which put the aggregate first: `{Name}JpaRepository` / `{Name}JpaQueryRepository` /
  `{Name}JpaRepositoryAdapter` (see [JPA Persistence Adapter](jpa-persistence-adapter.md)).
- REST controllers: class name suffix `…ApiV{version}` (e.g., `CodeApiV1`, `AssetApiV1`).
  Versioned via `@RequestMapping(version = "…")` rather than URL path prefix.
  See [Web API Conventions](web-api.md) for details.
