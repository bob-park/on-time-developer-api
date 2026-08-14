---
title: Directory Structure
scope: src/main/java/**
applies_to: creating new files or packages
related:
  - ./architecture.md
  - ./documentation.md
  - ./conventions/web-api.md
---

# Directory Structure

> Canonical dir/package tree; `{module}`/`{system}` placeholders. Read when creating new files or packages.

Canonical layout (mirrors `README.md`):

```text
├── docs
│   ├── agents         # Agent guide docs (conventions / libs / workflow)
│   ├── apis           # OpenAPI 3 specs
│   ├── domain         # Domain docs + glossary.md + Mermaid diagrams
│   └── superpowers    # Superpowers skill artifacts
│       ├── specs      # Design specs (YYYY-MM-DD-<topic>-design.md)
│       └── plans      # Implementation plans (YYYY-MM-DD-<topic>.md)
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── malgn
    │   │           ├── Application.java
    │   │           ├── config                    # App-wide configuration (feign / jpa / security / web mvc)
    │   │           │   ├── feign
    │   │           │   │   └── decoder
    │   │           │   ├── jpa
    │   │           │   └── security
    │   │           │       ├── converter
    │   │           │       └── manager
    │   │           └── {module}                  # Spring Modulith module (e.g., storages)
    │   │               ├── adapter
    │   │               │   ├── in
    │   │               │   │   └── web
    │   │               │   │       ├── api
    │   │               │   │       │   └── v1    # {Name}ApiV1 + nested {Parent}{Child}ApiV1 — single package per version
    │   │               │   │       │       └── dto  # Request/Response DTOs shared by the version's controllers
    │   │               │   │       └── view      # Server-rendered {Feature}View controllers
    │   │               │   │           └── dto   # Form / view-model records
    │   │               │   └── out
    │   │               │       ├── persistence
    │   │               │       │   └── jpa       # {Name}JpaRepository / {Name}JpaRepositoryAdapter
    │   │               │       │       └── query # {Name}JpaQueryRepository (+ query/impl for QueryDSL impls)
    │   │               │       └── {system}      # Other outbound adapters (e.g., aws.s3)
    │   │               ├── application
    │   │               │   ├── provided          # Inbound ports: {Name}Query / {Name}Register / {Name}Editor
    │   │               │   │   └── model         # {Name}Result / {Name}{Action}Command / {Name}QueryCriteria
    │   │               │   ├── required          # Outbound ports: {Name}Repository
    │   │               │   │   └── model         # {Name}QueryCondition / other outbound request models
    │   │               │   └── service           # {Name}QueryService / {Name}CommandService
    │   │               ├── config                # Module-scoped @Configuration + {Name}ExceptionHandler
    │   │               │   └── properties        # @ConfigurationProperties classes
    │   │               └── domain                # Domain model (only for modules that have one)
    │   │                   └── events            # Domain event records (@NamedInterface("domain-events"))
    │   └── resources
    │       ├── application.yml
    │       └── db
    │           └── migration                     # Flyway migration scripts
    └── test
        └── java
            └── com
                └── malgn                         # ApplicationTests / ApplicationModularityTests
```

`{module}` is a Spring Modulith module / bounded-context name (e.g., `storages`). `{system}` is an external system name (e.g., `aws.s3`, `aws.cloudfront`).
