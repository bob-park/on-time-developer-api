# AGENTS.md

Agent-agnostic project guide for `template-malgn-spring-boot-repository`. Detailed rules are split into focused topic files under [`docs/agents/`](docs/agents/) so an agent loads only what a task needs.

Each topic doc opens with YAML frontmatter (`title`, `scope`, `applies_to`, `related`) and a one-line TL;DR blockquote, so skimming the first few lines tells you whether the doc applies to your task. Find the matching entry in the Map below, open that doc, and check its TL;DR before reading on. If nothing matches, start with [overview](docs/agents/overview.md) and [architecture](docs/agents/architecture.md).

## Map

### Overview
- [Project Overview & Tech Stack](docs/agents/overview.md) — what the project is + tech stack/versions; read on onboarding or when checking a library version

### Architecture & Structure
- [Architecture](docs/agents/architecture.md) — Spring Modulith modules + hexagonal roles per package inside each module; read when deciding which package a class belongs in
- [Directory Structure](docs/agents/structure-dir.md) — canonical dir/package tree; read when creating files or packages

### Code Conventions
- [Annotation Order](docs/agents/conventions/annotation-order.md) — grouping/order of Lombok, JPA, Spring annotations
- [Import Order](docs/agents/conventions/import-order.md) — import group ordering
- [Builder Pattern](docs/agents/conventions/builder-pattern.md) — `@Builder` on private constructor + validation
- [Lombok Usage](docs/agents/conventions/lombok.md) — allowed/forbidden Lombok usage
- [Naming](docs/agents/conventions/naming.md) — package/class/port/adapter/controller/DTO naming
- [Null & Validation](docs/agents/conventions/null-validation.md) — `@Nullable`, `@Validated`, Guava checks
- [Logging](docs/agents/conventions/logging.md) — SLF4J via `@Slf4j`, parameterized messages
- [Web API](docs/agents/conventions/web-api.md) — REST controller naming/versioning, DTOs, PagedModel
- [Web View](docs/agents/conventions/web-view.md) — server-rendered view controllers under adapter/in/web/view
- [JPA Entity](docs/agents/conventions/jpa-entity.md) — entity base class/identity, construction, associations
- [JPA Persistence Adapter](docs/agents/conventions/jpa-persistence-adapter.md) — repository layering, QueryDSL idioms, paging
- [Exception Handling](docs/agents/conventions/exception-handling.md) — domain error codes/exceptions → RFC 9457 `ProblemDetail`
- [Application Service](docs/agents/conventions/application-service.md) — Command/Query split, transactions, port injection
- [Domain Model Behavior](docs/agents/conventions/domain-model-behavior.md) — status transitions, soft delete, association wiring
- [API Docs](docs/agents/conventions/api-docs.md) — OpenAPI 3.1 authoring under `docs/apis/`
- [Modulith Boundaries](docs/agents/conventions/modulith-boundaries.md) — module roots, @ApplicationModule/@NamedInterface, allowed cross-module access
- [Domain Events](docs/agents/conventions/domain-events.md) — event records in domain/events, publish/consume across modules

### Libraries
- [QueryDSL Pageable Sorting](docs/agents/libs/jpa-querydsl-sorts.md) — `Pageable` ordering via `QueryRepositoryUtils.sort` + `QueryDslPath`

### Workflow
- [Pull Requests](docs/agents/workflow/pr.md) — PR base branch + title rules (title drives version auto-bump)
- [Versioning](docs/agents/workflow/versioning.md) — release version pattern + auto-bump mechanism
- [Branch Naming](docs/agents/workflow/branching.md) — branch naming rules
- [Commit Messages](docs/agents/workflow/commit-messages.md) — commit message prefixes and format

### Ops
- [Build, Run & Test](docs/agents/build-run.md) — Gradle build/run/test + multi-arch Docker bake
- [Testing](docs/agents/testing.md) — JUnit 5, slice-test preference, test placement
- [Security & Auth](docs/agents/security.md) — OAuth2 resource server, authority mapping, authz rules, OpenFGA access control
- [Configuration & Secrets](docs/agents/configuration.md) — `application*.yml`, secrets sourcing
- [Documentation](docs/agents/documentation.md) — where API specs, domain docs, superpowers artifacts live

## How to use

- New REST endpoint → [web-api](docs/agents/conventions/web-api.md) → [application-service](docs/agents/conventions/application-service.md) → [jpa-persistence-adapter](docs/agents/conventions/jpa-persistence-adapter.md)
- New JPA entity → [jpa-entity](docs/agents/conventions/jpa-entity.md) → [domain-model-behavior](docs/agents/conventions/domain-model-behavior.md)
- New business error → [exception-handling](docs/agents/conventions/exception-handling.md)
- Paged/sorted query → [jpa-persistence-adapter](docs/agents/conventions/jpa-persistence-adapter.md) → [jpa-querydsl-sorts](docs/agents/libs/jpa-querydsl-sorts.md)
- New API spec → [api-docs](docs/agents/conventions/api-docs.md)
- Deciding which package a class belongs in → [architecture](docs/agents/architecture.md) → [structure-dir](docs/agents/structure-dir.md)
- New module / cross-module dependency → [modulith-boundaries](docs/agents/conventions/modulith-boundaries.md) → [architecture](docs/agents/architecture.md)
- Cross-module communication / new domain event → [domain-events](docs/agents/conventions/domain-events.md) → [modulith-boundaries](docs/agents/conventions/modulith-boundaries.md)
- New server-rendered page → [web-view](docs/agents/conventions/web-view.md)
- Opening a PR → [pr](docs/agents/workflow/pr.md) (base branch + title)
- Starting a branch → [branching](docs/agents/workflow/branching.md)
- Writing a commit message → [commit-messages](docs/agents/workflow/commit-messages.md)
- Cutting a release → [versioning](docs/agents/workflow/versioning.md)
- Building / running / packaging → [build-run](docs/agents/build-run.md)
- Touching security or config → [security](docs/agents/security.md) / [configuration](docs/agents/configuration.md)

## Convention Enforcement

These conventions apply to **new code and to existing code being substantially modified**. Pre-existing code that does not yet conform must **not** be refactored as part of unrelated changes — track it separately (e.g., a follow-up note in the PR description, or its own `refactor:` PR per [commit-messages](docs/agents/workflow/commit-messages.md)). Where an example shows a pattern an existing class does not implement, the example is the target for new code, not a directive to backfill the old class.
