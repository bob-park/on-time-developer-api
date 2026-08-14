---
title: Domain Model Behavior Conventions
scope: src/main/java/**/*.java
applies_to: adding a state transition, soft delete, or association setter to an entity
related:
  - ./jpa-entity.md
  - ./jpa-persistence-adapter.md
  - ./exception-handling.md
---

# Domain Model Behavior Conventions

> Entity behavior — status-transition methods, the soft-delete contract, and association-wiring methods. Read when adding a state transition, soft delete, or association setter to an entity.

Behavior lives on the entity. Entities model their own state transitions, soft delete, and association wiring instead of exposing public setters.

## Status-Transition Methods

State changes are named methods on the entity, not external setters. Each transition:

1. Returns early (idempotent no-op) when the entity is already in the target state.
2. Throws `Invalid{Domain}StatusException(from, target)` when the current state is not a valid source for this transition.
3. Otherwise assigns the new state.

Shape:

```java
public void <transition>() {
    if (status == <TARGET>) {
        return;
    }
    if (status != <REQUIRED_SOURCE>) {
        throw new Invalid<Domain>StatusException(status, <TARGET>);
    }
    this.status = <TARGET>;
}
```

Example (`SessionChair`):

```java
public void invite() {
    if (status == SessionChairStatus.INVITED) {
        return;
    }
    if (status != SessionChairStatus.REGISTERED) {
        throw new InvalidSessionChairStatusException(status, SessionChairStatus.INVITED);
    }
    this.status = SessionChairStatus.INVITED;
}

public void accept() {
    if (status == SessionChairStatus.ACCEPTED) {
        return;
    }
    if (status != SessionChairStatus.INVITED) {
        throw new InvalidSessionChairStatusException(status, SessionChairStatus.ACCEPTED);
    }
    this.status = SessionChairStatus.ACCEPTED;
}
```

`Invalid{Domain}StatusException` is a domain exception per [exception-handling](exception-handling.md) — its `ErrorCode` carries `HttpStatus.BAD_REQUEST`.

## Soft-Delete Contract

Entities are soft-deleted, never hard-deleted. (The persistence layer's default `eqDeleted(false)` query filter is covered in [jpa-persistence-adapter](jpa-persistence-adapter.md).)

- `private Boolean deleted` (boxed), initialized in the constructor with `this.deleted = getIfNull(deleted, false)`.
- A null-safe read helper overrides the Lombok getter intent: `public boolean isDeleted() { return Boolean.TRUE.equals(getDeleted()); }`.
- `public void delete()` throws `com.malgn.starter.common.exception.AlreadyExecuteException` when already deleted, then sets the flag.

```java
public void delete() {
    if (isDeleted()) {
        throw new AlreadyExecuteException("already deleted.");
    }

    this.deleted = true;
}

public boolean isDeleted() {
    return Boolean.TRUE.equals(getDeleted());
}
```

## Association-Wiring Methods

The owning side of an association is set through an `assign{X}(...)` method, not a public setter. The aggregate root's `addX` helper (see [jpa-entity](jpa-entity.md)) calls the child's `assign{X}(this)`, so a single `addX` call keeps both sides in sync. A nullable association takes a `@Nullable` parameter.

```java
public void assignParent(Code parent) {
    checkArgument(parent != null, "parent must be provided.");
    this.parent = parent;
}

public void addChild(Code child) {
    checkArgument(child != null, "child must be provided.");

    getChildren().add(child);
    child.assignParent(this);
}
```
