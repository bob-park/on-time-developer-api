---
title: Builder Pattern
scope: src/main/java/**/*.java
applies_to: adding `@Builder` to a class or record
related:
  - ./null-validation.md
  - ./jpa-entity.md
---

# Builder Pattern

> `@Builder` on private constructor + validation; record variant. Read when adding `@Builder` to a class or record.

When using `@Builder` on a class:

- Declare `@Builder` on the **constructor**, not on the class. The constructor's parameter list defines the builder's API and lets validation run when the builder finishes.
- The constructor **must be `private`**.
- Required-field validation happens in that constructor using Guava's `Preconditions.checkArgument` together with `org.apache.commons.lang3.StringUtils`.

```java
import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;

public class Asset {

    private final String name;

    @Builder
    private Asset(String name) {
        checkArgument(StringUtils.isNotBlank(name), "name must be provided.");
        this.name = name;
    }
}
```

For `record` types with `@Builder`, keep `@Builder` on the record itself and put validation in a **compact constructor** (records cannot have a `private` canonical constructor):

```java
@Builder
public record AssetFileMeta(String key,
                            Long contentLength,
                            LocalDateTime lastModifiedDate) {

    public AssetFileMeta {
        checkArgument(StringUtils.isNotBlank(key), "key must be provided.");
    }
}
```
