---
title: Annotation Order
scope: src/main/java/**/*.java
applies_to: adding annotations to a class
related:
  - ./import-order.md
  - ./lombok.md
  - ./jpa-entity.md
---

# Annotation Order

> Grouping/order of Lombok, JPA, Spring annotations. Read when adding annotations to a class.

Annotations are grouped, in this order, with **no blank line between groups** (the annotations form one contiguous block above the type declaration):

1. **Lombok** (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`, `@Getter`, `@NoArgsConstructor`, …)
2. **JPA / Entity** (`@Entity`, `@Table`, `@Embeddable`, `@MappedSuperclass`, …) — only when the class is a JPA entity
3. **Spring** (`@Configuration`, `@Component`, `@Service`, `@Repository`, `@RestController`, `@Bean`, `@Transactional`, `@Validated`, …)

Within a group, ordering is free, but keep stereotype annotations (`@Configuration`, `@RestController`, `@Service`, …) at the bottom of the Spring group.

JPA entity example:

```java
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "asset")
public class Asset extends BaseEntity<Long> { ... }
```

Spring component example:

```java
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AssetService { ... }
```
