---
title: Testing
scope: ["build.gradle", "src/test/**"]
applies_to: writing or running tests
related:
  - ./build-run.md
---

# Testing

> JUnit 5, slice-test preference, test placement. Read when writing or running tests.

- JUnit 5 (`useJUnitPlatform()`).
- `spring-boot-starter-test` + `spring-security-test`.
- Test classes mirror the production package.
- Prefer slice tests (`@WebMvcTest`, `@DataJpaTest`) over full `@SpringBootTest` when scope allows.
