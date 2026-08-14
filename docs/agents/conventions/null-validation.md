---
title: Null & Validation
scope: src/main/java/**/*.java
applies_to: handling nullable values or validating input/invariants
related:
  - ./builder-pattern.md
---

# Null & Validation

> `@Nullable`, `@Validated`, Guava `checkArgument`/`checkState`. Read when handling nullable values or validating input/invariants.

- Use `org.jspecify.annotations.@Nullable` for nullable parameters and return types.
- Input validation in adapters: `@Validated` plus `jakarta.validation.constraints.*`.
- Exception: application-layer provided/required port interfaces use neither
  `org.jspecify.annotations.*` nor `jakarta.validation.constraints.*` — keep port method
  signatures annotation-free.
- Invariant checks inside domain / application code: Guava `checkArgument` / `checkState`.
