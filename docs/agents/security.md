---
title: Security & Auth
scope: ["src/main/resources/**", "src/main/java/**/config/security/**"]
applies_to: touching security config or authorization
related:
  - ./configuration.md
  - ./architecture.md
---

# Security & Auth

> OAuth2 resource server, authority mapping, request authz rules, OpenFGA access control. Read when touching security config or authorization.

- OAuth 2.0 Resource Server (JWT) configured under `config.security`.
- Custom authority mapping in `JwtRoleGrantAuthoritiesConverter`: scopes become `SCOPE_*`; scope values that already start with `ROLE_` pass through.
- When adding new request-based authorization rules, extend `config.security.manager.ConnectionBaseManager` — it handles request matching and role-hierarchy reachability for `ROLE_MANAGER`.

## Package Layout under `config.security`

`config.security` holds the global (non-module-specific) security wiring. Provider-agnostic
strategy types sit at each sub-package root; provider- or area-specific leaves get their own nested
package:

- `config.security.converter` — authority and social-login user converters. Each identity
  provider's leaf converter lives in its own sub-package `config.security.converter.{provider}`.
- `config.security.manager` — request-authorization managers (`ConnectionBaseManager` and its
  concrete subclasses); area-specific managers nest under `manager.{area}`.
- `config.security.handler` — authentication success / failure handlers.
- `config.security.model` — security value objects and principals (records such as the resolved
  provider-user and the authenticated principal).
- `config.security.service` — Spring Security user-loading services (`UserDetailsService`,
  OAuth2 and OIDC user services).

Give each `SecurityFilterChain` its own `@Configuration` class named `{Concern}SecurityConfiguration`
(e.g. the JWT resource-server chain vs. the browser/login chain). Strategy classes (handlers,
converters, managers, user services) are registered as `@Bean`s from the configuration that uses
them.

## Social-Login Provider Converter

Mapping each identity provider's response to a common principal uses a delegating converter with one
leaf per provider:

- `ProviderUserConverter` — the interface. `convert(request)` maps a provider response to the common
  provider-user model; `isSupport(providerType)` defaults to `false`.
- `DelegatingProviderUserConverter` — the delegator. It resolves the provider from the
  `ClientRegistration` registration id, then delegates to the first leaf whose `isSupport(...)`
  returns `true`, and fails when no leaf supports the provider.
- One leaf per provider — `{Provider}ProviderUserConverter` in
  `config.security.converter.{provider}`, stateless, mapping that provider's claims and returning
  `true` from `isSupport(...)` only for its own provider constant.

Add a provider by dropping a new leaf converter into a new `config.security.converter.{provider}`
package and registering it with the delegator — no existing leaf changes.

## Global Config vs. Module-Local Security Adapters

Two layers cooperate; keep them separate:

- **`config.security`** owns the cross-cutting wiring — filter chains, authority mapping,
  request-authorization managers, handlers, and the social-login converter/service strategy beans.
  It is not tied to any single module.
- **`{module}.adapter.in.security`** holds a module's inbound security adapters: classes that
  implement a framework SPI (e.g. an OAuth2 Authorization Server SPI) over that module's own
  `application.{module}.required` ports and `domain` types. These are hexagonal inbound adapters
  (`@Component` / `@Service`, `@RequiredArgsConstructor`, class-level
  `@Transactional(readOnly = true)` with per-method write overrides) — see
  [Architecture](architecture.md). Spring discovers them as beans; `config.security` consumes
  them indirectly through the framework, never by importing module internals.

## OpenFGA Access Control

`malgn-spring-boot-starter` auto-configures OpenFGA-based relationship access control
(`OpenFgaAutoConfiguration`), active only when `malgn.auth.openfga.enabled` is `on`.
`store-id` and `model-id` are required; `host` defaults to `http://localhost:8080`
(`OpenFgaProperties`, prefix `malgn.auth.openfga`).

Beans (all `@ConditionalOnMissingBean`, overridable by the application):

- `fga` (`OpenFga`) — SpEL entry point for `@PreAuthorize`.
- `AccessControl` (`OpenFgaAccessControl`) — programmatic port: `check`, `listAccessible`,
  `write`, `remove`.

Rules:

- Controller authorization: `@PreAuthorize("@fga.check('<objectType>', #id, '<relation>')")`.
  The 3-arg form resolves the current user from the `SecurityContext` as
  `user:{authentication.name}`; overloads accept a `RelationType` or an explicit
  `userType` / `userId` pair.
- Service logic: inject `AccessControl` and build `AccessControlQueryRequest` /
  `AccessControlListQueryRequest` via their builders.
- Resources whose permissions live in OpenFGA MUST sync tuples on create / update / delete —
  call `write` / `remove` with `AccessControlWriteRequest` / `AccessControlRemoveRequest`
  in the same use case that mutates the resource.
