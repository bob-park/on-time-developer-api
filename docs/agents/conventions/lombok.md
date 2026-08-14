---
title: Lombok Usage
scope: src/main/java/**/*.java
applies_to: using Lombok annotations
related:
  - ./annotation-order.md
  - ./jpa-entity.md
---

# Lombok Usage

> Allowed/forbidden Lombok usage. Read when using Lombok annotations.

- Prefer `@RequiredArgsConstructor` for dependency injection (constructor injection).
- Use `@Slf4j` for logging — never declare a logger field by hand.
- Do **not** use `@Data` on entities. Use `@Getter` + `@NoArgsConstructor(access = AccessLevel.PROTECTED)` instead.
- `@AllArgsConstructor` on public types is discouraged. If needed, scope it: `@AllArgsConstructor(access = AccessLevel.PRIVATE)`.
