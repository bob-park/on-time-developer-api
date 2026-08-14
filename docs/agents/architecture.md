---
title: Architecture
scope: src/main/java/**
applies_to: deciding which package a new class belongs in
related:
  - ./overview.md
  - ./structure-dir.md
  - ./conventions/naming.md
---

# Architecture

> Spring Modulith modules, each with an internal hexagonal structure (adapter/application/config/domain). Read when deciding which package a new class belongs in.

The project is a **Spring Modulith** application following **Hexagonal Architecture** combined with the **Domain Model Pattern**. Under `com.malgn`, the top level consists of `config` plus one package per module:

- `config` — App-wide Spring `@Configuration` (security/OAuth2, JPA, Feign, Web MVC). No business logic. Not a Modulith module.
- `{module}` (e.g., `storages`) — A Spring Modulith module (bounded context). Module boundaries are verified by `ApplicationModularityTests`.

Inside each module, packages correspond to hexagonal roles:

- `{module}.config` — Module-scoped `@Configuration` (+ `properties` for `@ConfigurationProperties`) and the module's `{Name}ExceptionHandler` REST advice. No business logic.
- `{module}.domain` — Pure domain model (entities, value objects, domain services), for modules that have one. No framework leakage outward; JPA mapping annotations are allowed because the domain entities double as JPA entities, but the package depends on nothing in `adapter` or `application`.
- `{module}.application` — Use cases:
  - `application.provided` — inbound ports the module exposes (+ `model` for commands / results).
  - `application.required` — outbound ports the module needs (+ `model` for requests / responses).
  - `application.service` — use case implementations of the `provided` ports.
- `{module}.adapter` — Concrete adapters:
  - `adapter.in.web.api` — REST controllers and request/response DTOs.
  - `adapter.out.{system}` — outbound adapters implementing `required` ports (e.g., `aws.s3`, `aws.cloudfront`, JPA persistence).
