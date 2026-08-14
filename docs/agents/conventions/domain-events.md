---
title: Domain Events
scope: src/main/java/**/*.java
applies_to: publishing or consuming a domain event across modules
related:
  - ../architecture.md
  - ../structure-dir.md
  - ./modulith-boundaries.md
  - ./application-service.md
---

# Domain Events

> Immutable `record` events in `domain.events`, published from the transactional application service, consumed via `@ApplicationModuleListener`. Read when publishing or reacting to a cross-module fact.

Domain events are the decoupled alternative to a direct cross-module port call: instead of module B
calling into module A, module A **publishes** a fact and any interested module **consumes** it. This
keeps the compile-time dependency graph one-directional and is the preferred way to invert a would-be
cycle (see [Modulith Boundaries](./modulith-boundaries.md)).

## Event Types Live in `domain.events` as Immutable Records

- Place events in `{module}.domain.events` — they are part of the domain (hexagonal core).
- An event is an immutable Java `record` with `@Builder` (Lombok). Fields carry only the data a
  consumer needs; identifier fields are exposed as `String` (stringify the numeric PK with
  `String.valueOf(...)`), consistent with the ID rule in [Web API](./web-api.md).
- Expose the package as `@NamedInterface("domain-events")` so other modules may reference the event
  types — apply this to **every** `domain.events` package, not just some
  (see [Modulith Boundaries](./modulith-boundaries.md)).

```java
package com.malgn.codes.domain.events;

import lombok.Builder;

@Builder
public record CodeDeleted(String id,
                          String name) {
}
```

## Naming: `<Aggregate><PastTenseVerb>`, No `Event` Suffix

Name an event after the completed fact it records: the aggregate name followed by a **past-tense
verb**, in PascalCase, with **no** `Event` suffix — `CodeDeleted`, `RaffleClosed`,
`RaffleEntrySubmitted`. The name states what already happened, so consumers read it as a fact, not a
command.

## Publish from the Transactional Application Service

Inject `org.springframework.context.ApplicationEventPublisher` (field named `publisher`, `final`, via
`@RequiredArgsConstructor`) into the module's application service, and publish **after** mutating the
aggregate, inside the same `@Transactional` unit of work. The application service — not a controller,
adapter, or framework callback — is the canonical publish site (see
[Application Service](./application-service.md)).

```java
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CodeCommandService implements CodeRegister, CodeEditor {

    private final CodeRepository codeRepository;
    private final ApplicationEventPublisher publisher;

    @Override
    public CodeResult deleteCode(Long id) {

        Code code =
            codeRepository.findCode(id)
                .orElseThrow(() -> new NotFoundException(Id.of(Code.class, id)));

        code.delete();

        log.debug("deleted code. ({})", code);

        publisher.publishEvent(
            CodeDeleted.builder()
                .id(String.valueOf(code.getId()))
                .name(code.getName())
                .build());

        return CodeResult.from(code);
    }
}
```

## Consume with `@ApplicationModuleListener`

React to another module's event with a listener annotated
`org.springframework.modulith.ApplicationModuleListener`. It is meta-annotated to run
**after the publishing transaction commits**, asynchronously, in its own new transaction — so a
consumer failure never rolls back the producer. Keep the listener package-private (`@Component`,
constructor-injected), and delegate real work to that module's own application-layer port.

```java
package com.malgn.raffles.application.service;

import com.malgn.codes.domain.events.CodeDeleted;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.modulith.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class CodeDeletedListener {

    private final RaffleEditor raffleEditor;

    @ApplicationModuleListener
    void on(CodeDeleted event) {
        log.debug("handling code deleted. ({})", event);
        raffleEditor.detachCode(Long.valueOf(event.id()));
    }
}
```

If the consuming module declares the optional `allowedDependencies` whitelist (see
[Modulith Boundaries](./modulith-boundaries.md)), it must include the producer's events interface:
`@ApplicationModule(allowedDependencies = { ..., "codes :: domain-events" })`. Without the
whitelist no extra declaration is needed — `domain.events` is already a named interface.

## Transactional Outbox

`@ApplicationModuleListener` delivery is backed by the Spring Modulith **event publication registry**
(a transactional outbox): with `spring-modulith-starter-jpa` on the classpath, each event is
persisted in the same transaction as the publish and marked complete once its listener succeeds. This
makes cross-module reactions durable — an unprocessed event survives a restart. Enable
`republish-outstanding-events-on-restart` and `completion-mode: update` under `spring.modulith.events`
for at-least-once redelivery.

## Externalization Is Optional

Publishing an event to an external broker (e.g. Kafka) is a separate, optional integration on top of
the in-process listener contract — add it only when another system must consume the event. Annotate
the record with `@Externalized("<namespace>.<entity>.<action>::#{#this.id}")`, where the string before
`::` is the topic and the SpEL after `::` is the routing key. This requires `spring-modulith-events-*`
externalization dependencies; adding them is a dependency change and needs sign-off. In-module and
cross-module reactions never depend on externalization — they use `@ApplicationModuleListener`.

```java
import lombok.Builder;

import org.springframework.modulith.events.Externalized;

@Builder
@Externalized("code.codes.deleted::#{#this.id}")
public record CodeDeleted(String id,
                          String name) {
}
```
