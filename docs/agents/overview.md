---
title: Project Overview & Tech Stack
scope: src/main/java/**
applies_to: onboarding, or checking a library/framework version
related:
  - ./architecture.md
  - ./structure-dir.md
---

# Project Overview & Tech Stack

> What the project is plus the full tech stack and versions. Read when onboarding or checking a library version.

## Project Overview

`template-malgn-spring-boot-repository` is a Spring Boot project template built on top of `malgn-spring-boot-starter`. It is intended as a starting point for new services and ships with sensible defaults for security, persistence, observability, and AWS integration.

## Tech Stack

- Java 25 (managed via `sdkman`, see `.sdkmanrc`)
- Spring Boot 4 (`org.springframework.boot:4.0.0`)
- Spring Cloud 2025.1.0 (Consul discovery + config, OpenFeign, Resilience4j)
- JPA + QueryDSL 7.1
- OAuth 2.0 Resource Server (KeyFlow OAuth 2.0 Authorization Server)
- p6spy (`p6spy-spring-boot-starter`)
- PostgreSQL JDBC (`org.postgresql:postgresql`)
- AWS SDK v2 via `spring-cloud-aws-starter-s3`
- Lombok 1.18.42
- `com.malgn:malgn-spring-boot-starter`
