---
title: JPA Entity Conventions
scope: src/main/java/**/*.java
applies_to: creating or modifying a JPA entity
related:
  - ./builder-pattern.md
  - ./null-validation.md
  - ./jpa-persistence-adapter.md
  - ./domain-model-behavior.md
---

# JPA Entity Conventions

> Entity base class/identity, construction, associations, toString. Read when creating or modifying a JPA entity.

Rules for `domain.{name}` JPA entities. Applies to all new entities; see [Builder Pattern](builder-pattern.md) for the general builder rule this section builds on.

## Base Class & Identity

- Extend `com.malgn.starter.common.entity.BaseEntity<ID>` (`ID` = the `@Id` field type, normally `Long`). `BaseEntity` supplies auditing (`createdBy`, `lastModifiedBy`, `createdDate`, `lastModifiedDate`) and `Persistable.isNew()` handling so Spring Data treats pre-assigned Snowflake ids as new rows.
- Declare the `@Id` field **on the entity itself** (it is not inherited).
- The `@Id` field **must** be annotated `@SnowflakeIdGenerateValue` (from `com.malgn.starter.common.entity.annotation`). This is the project-wide identity strategy — every JPA entity's primary key is a Snowflake id assigned by this annotation. Do not use `@GeneratedValue`, sequences, or `IDENTITY`.

## Construction

- Build via `@Builder` on a `private` constructor per [Builder Pattern](builder-pattern.md) — do not repeat the builder mechanics here, they are identical.
- Required-field invariants: Guava `checkArgument` + Apache `StringUtils` (static import `com.google.common.base.Preconditions.checkArgument`).
- Nullable fields with a default: assign `getIfNull(value, default)` (static import `org.apache.commons.lang3.ObjectUtils.getIfNull`; the `(T, T)` overload).
- The builder accepts `id` so tests and seed data can pass an explicit id; production code omits it and lets `@SnowflakeIdGenerateValue` assign one.
- Nullable parameters/fields carry `org.jspecify.annotations.@Nullable` ([Null & Validation](null-validation.md)).

## Associations (bidirectional, mandatory)

- Every association is **bidirectional**. Single-sided mappings are not used.
- The side that owns the foreign key (`@ManyToOne` + `@JoinColumn`, or `@OneToOne` + `@JoinColumn`) is the **owning** side. The other side is the **inverse** side and declares `mappedBy`.
- `fetch` is **always explicitly `FetchType.LAZY`**, including on `@ManyToOne` and `@OneToOne` whose JPA default is `EAGER`.
- `cascade` is declared explicitly to match intent. Never apply a blanket `CascadeType.ALL`. Use `orphanRemoval = true` only when the child's lifecycle is genuinely owned by the parent.
- Collection associations are field-initialized (`= new ArrayList<>()`) and never reassigned.
- The owning aggregate exposes synchronization helpers (`addX` / `removeX`) that mutate **both** sides. Never mutate one side in isolation. The child's owning side is set through an `assign{X}(...)` method that `addX` calls — see [domain-model-behavior](domain-model-behavior.md).

## toString Safety

- Annotate the class with `@ToString`.
- Annotate **every association field** with Lombok's `@ToString.Exclude`. Import it as `import lombok.ToString.Exclude;` and use the short form `@Exclude` on the field.
- Rationale: a bidirectional graph plus an all-fields `@ToString` causes infinite recursion and triggers `LazyInitializationException` on lazy associations.

## Behavior & Lifecycle

Entity behavior — status-transition methods, the soft-delete contract (`deleted` flag, null-safe `isDeleted()`, `delete()` throwing `AlreadyExecuteException`), and `assign{X}(...)` association wiring — is documented in [domain-model-behavior](domain-model-behavior.md). Keep that behavior on the entity rather than exposing public setters.

## Canonical Example

```java
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.ToString.Exclude;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import com.malgn.starter.common.entity.BaseEntity;
import com.malgn.starter.common.entity.annotation.SnowflakeIdGenerateValue;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.ObjectUtils.getIfNull;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "codes")
public class Code extends BaseEntity<Long> {

    @Id
    @SnowflakeIdGenerateValue
    private Long id;

    private String name;

    private String displayName;

    @Nullable
    private String description;

    private Boolean deleted;

    @Nullable
    @Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Code parent;

    @Exclude
    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
    private List<Code> children = new ArrayList<>();

    @Builder
    private Code(Long id,
                 String name,
                 String displayName,
                 @Nullable String description,
                 Boolean deleted) {

        checkArgument(StringUtils.isNotBlank(name), "name must be provided.");
        checkArgument(StringUtils.isNotBlank(displayName), "displayName must be provided.");

        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.deleted = getIfNull(deleted, false);
    }

    public void addChild(Code child) {
        checkArgument(child != null, "child must be provided.");

        this.children.add(child);
        child.parent = this;
    }

    public void removeChild(Code child) {
        checkArgument(child != null, "child must be provided.");

        this.children.remove(child);
        child.parent = null;
    }
}
```
