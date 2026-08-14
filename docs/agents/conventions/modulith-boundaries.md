---
title: Modulith Boundaries
scope: src/main/java/**/*.java
applies_to: defining a module boundary, root package-info, named interface, or cross-module dependency
related:
  - ../architecture.md
  - ../structure-dir.md
  - ./domain-events.md
  - ./naming.md
---

# Modulith Boundaries

> Module root `@ApplicationModule` declarations, `@NamedInterface` port surfaces, and the cross-module import rule. Read when defining a module boundary or reaching into another module.

Every top-level package under `com.malgn` is a Spring Modulith module — business modules are
bounded contexts with the internal hexagonal split described in [Architecture](../architecture.md),
while `commons` and `config` are OPEN shared-infrastructure modules (each declared
`@ApplicationModule(type = Type.OPEN)`, e.g. `com.malgn.config`'s `package-info.java`).
A module's boundary is not implicit: it is declared at the module root and enforced by
`ApplicationModularityTests` (`ApplicationModules.of(Application.class).verify()`). These rules define
that boundary for new code.

## Every Module Root Declares `@ApplicationModule`

Every module carries a root `package-info.java` with an explicit `@ApplicationModule`. Do **not**
rely on Modulith's implicit default (base-package sub-package ⇒ one `CLOSED` module) — declare the
boundary so the module's display name and type are visible and verifiable.

- `displayName` — human-readable module name for generated docs / module canvases. It may be
  written in the domain language (한국어) when that reads better in generated documentation.
- Business modules stay `CLOSED` (the default type): nothing is reachable except through a
  published named interface.

```java
@ApplicationModule(displayName = "Raffles")
package com.malgn.raffles;

import org.springframework.modulith.ApplicationModule;
```

## `allowedDependencies` Is Optional — Declare It Only When Needed

`allowedDependencies` is **not required**; the default is to omit it. Boundaries are already
enforced by `CLOSED` modules + named interfaces + `modules.verify()`. Declare the whitelist only
when a module's dependency surface should be pinned explicitly — e.g., a module whose dependency
set must not grow silently.

When declared, list dependencies at named-interface granularity using the
`"module :: named-interface"` syntax. A bare `"module"` entry allows only that module's default
interface; name the interface (`"codes :: provided"`) to allow a specific port surface. The list is
exhaustive for that module: any edge not on it fails `modules.verify()`.

- Add an entry only for a dependency the module actually has, and only the named interfaces it
  actually imports (typically `provided` + `provided-model`, or `domain-events`).
- Open/shared infrastructure modules (below) are referenced by bare module name (`"commons"`).

```java
@ApplicationModule(
    displayName = "Raffles",
    allowedDependencies = { "commons", "codes :: provided", "codes :: provided-model" }
)
package com.malgn.raffles;

import org.springframework.modulith.ApplicationModule;
```

## Named Interfaces Are the Only Public Surface

A `CLOSED` module exposes **only** the packages marked `@NamedInterface`. Mark exactly these:

- `application.provided` → `@NamedInterface("provided")` — the inbound ports (`{Entity}Query`,
  `{Entity}Register`, `{Entity}Editor`) other modules call.
- `application.provided.model` → `@NamedInterface("provided-model")` — the command/result DTOs those
  ports accept and return (`{Entity}Result`, `{Entity}{Verb}Command`, `{Entity}QueryCriteria`).
- `domain.events` → `@NamedInterface("domain-events")` — published domain events, when the module has
  any (see [Domain Events](./domain-events.md)). Apply this consistently to **every** `domain.events`
  package, not just some modules.

```java
@NamedInterface("provided")
package com.malgn.raffles.application.provided;

import org.springframework.modulith.NamedInterface;
```

Named-interface literals are a fixed, lowercase kebab-case vocabulary: `"provided"`,
`"provided-model"`, `"domain-events"`. Everything else — `domain` (except `events`),
`application.required`, `application.service`, and all of `adapter` — stays module-internal and is
**never** annotated.

## Cross-Module Access Goes Through Named Interfaces Only

A module may reference another module **only** by importing its named-interface types:
`{other}.application.provided`, `{other}.application.provided.model`, or `{other}.domain.events`.
Inject the published port and consume its result DTOs; never touch the other module's internals.

```java
// allowed — consuming the codes module's published port
import com.malgn.codes.application.provided.CodeQuery;
import com.malgn.codes.application.provided.model.CodeResult;

// forbidden — reaching into another module's internals
import com.malgn.codes.domain.Code;                       // domain (non-events)
import com.malgn.codes.application.service.CodeQueryService; // application service
import com.malgn.codes.adapter.out.persistence.CodeJpaRepository; // adapter
```

The outbound call may originate from either the consuming module's web adapter or its application
service — but in both cases the imported type must be a named interface.

## Shared Infrastructure Is `Type.OPEN`

Cross-cutting infrastructure that many modules legitimately need — shared utilities (`commons`) and
app-wide `config` — is declared `@ApplicationModule(type = Type.OPEN)`. An OPEN module lets any
module depend on it and reach its sub-packages without those packages being named interfaces. Keep
OPEN modules free of business logic; use OPEN sparingly and only for genuine infrastructure.

```java
@ApplicationModule(displayName = "commons", type = Type.OPEN)
package com.malgn.commons;

import org.springframework.modulith.ApplicationModule;
import org.springframework.modulith.ApplicationModule.Type;
```

## Preventing Cycles

`modules.verify()` rejects dependency cycles. When two modules appear to need each other, do **not**
open a back-edge — break the cycle one of two ways:

- **Extract the shared abstraction.** Move the type both modules share into a shared/`commons`
  module (or a third module both may depend on), so both edges point the same direction.
- **Invert one direction with an event.** Replace the back-call with a domain event: the module that
  would have been called instead **publishes** an event, and the other module **consumes** it via
  `@ApplicationModuleListener` (see [Domain Events](./domain-events.md)). This removes the compile-
  time dependency entirely and keeps the modules decoupled.

Prefer the event inversion when the back-edge is a reaction to something happening (a state change),
and extraction when it is genuinely shared structure.
