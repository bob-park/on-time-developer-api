---
title: API Docs Conventions
scope: docs/apis/**
applies_to: writing or updating an API spec under `docs/apis/`
related:
  - ../documentation.md
  - ./web-api.md
  - ./exception-handling.md
---

# API Docs Conventions

> OpenAPI 3.1 authoring — root/split-file layout, `$ref` rules, explicit `required` marking, shared schemas, standard responses. Read when writing or updating an API spec under `docs/apis/`.

API specs live in `docs/apis/` and are authored as **OpenAPI 3.1.0**, split into many small files
that mirror the API package structure. The root file wires them together with `$ref`; it never
inlines path or schema bodies.

## Spec Version & Root File

- Spec version is exactly `openapi: 3.1.0`.
- A single root entry — `docs/apis/openapi3.{module-name}.yml` — owns `info`, `servers`, `security`,
  and `components.securitySchemes` (the `keyflow-auth` OAuth2 scheme). `{module-name}` is the Gradle
  module name: this template ships `openapi3.template.yml`; the `eventify-api` module would name its
  root `openapi3.eventify-api.yml`.
- The root `paths:` map **only `$ref`s out** to per-route files. No inline path bodies in the root.

```yaml
openapi: 3.1.0
info:
  title: Template REST API APIs
  version: 0.001-2026-0529
servers:
  - url: 'http://localhost:8080/api'
    description: Local Server
security:
  - keyflow-auth:
      - openid
      - profile
components:
  securitySchemes:
    keyflow-auth:
      type: oauth2
      # ...authorizationCode flow...
paths:
  # conferences
  /v1/conferences:
    $ref: './v1/conferences/path-conferences.yml'
  /v1/conferences/{id}:
    $ref: './v1/conferences/path-conferences-ids.yml'
```

## Directory Layout & File Naming

Split files mirror the API package structure (`{module}.adapter.in.web.api.v{version}`, see
[structure-dir](../structure-dir.md)):

```text
docs/apis/
├── openapi3.{module-name}.yml       # root: info, servers, security, paths ($ref only)
├── schema-error-response.yml        # shared ProblemDetail (RFC 9457)
├── schema-paged-model.yml           # shared Pageable
└── v1/{context}/
    ├── path-{resource}.yml              # collection routes (post / get)
    ├── path-{resource}-ids.yml          # item routes (get / put / delete)
    ├── path-{resource}-ids-{action}.yml # status-transition routes (accept / invite / …)
    ├── schema-{resource}-response.yml
    ├── schema-{resource}-{x}-request.yml
    └── schema-{resource}-status.yml     # enums
```

- One `path-*.yml` per **route**; the HTTP methods (`post`, `get`, `put`, `delete`) are keys inside it.
- One `schema-*.yml` per **DTO or enum**.
- `{id}` path segments map to the `-ids` filename suffix; nested actions append `-{action}`.
- Nested resources get sub-directories (e.g. `v1/presentations/comments/`,
  `v1/presentations/evaluations/summary/`).

## `$ref` Rules

- Use **relative paths only**.
- Sibling files in the same dir: `./schema-{resource}-response.yml`.
- Shared root schemas from a `v1/{context}/` file: `../../schema-error-response.yml`.
- A schema's `title` carries the `V1` suffix matching its DTO class (`ConferenceResponseV1`), so the
  generated component name lines up with the [web-api](web-api.md) DTO naming.

## Path File Conventions

Each operation declares, in order:

- `operationId` — **required**, unique across the whole spec. camelCase, English,
  `{verb}{Resource}[{Action}]V1`: the verb reflects the HTTP method (`list` / `get` for `get`,
  `register` / `create` for `post`, `update` for `put`, `delete` for `delete`), the resource is
  singular for item routes and plural for collection routes, and status-transition routes append the
  action (e.g. `listConferencesV1`, `registerConferenceV1`, `getConferenceV1`, `updateConferenceV1`,
  `acceptSessionChairV1`). The `V1` suffix matches the route version.
- `tags` — `{Context} V1` (e.g. `Conferences V1`, `Session Chairs V1`).
- `summary` — short Korean title (e.g. `컨퍼런스 등록`).
- `description` — Korean; **lead with the required role** when access is restricted
  (e.g. `관리자 권한(`ROLE_MANAGER`) 이상 필요`).
- `security` — `keyflow-auth` with `openid` / `profile` scopes.
- `parameters` / `requestBody` / `responses` as applicable.

Identifiers are serialized as `string` in schemas (Snowflake IDs exceed JS safe-integer range — see
the String-ID rule in [web-api](web-api.md)).

## Required Marking

Mark required-ness **explicitly everywhere** — never rely on the OpenAPI defaults:

- **Query string / path parameters:** every `parameters` entry declares `required: true` or
  `required: false` (path parameters are always `required: true`).
- **Request body:** the operation declares `requestBody.required` (`true`/`false`), and the request
  schema file lists every mandatory property in its `required:` array.
- **Response body:** the response schema file lists every always-present property in its
  `required:` array; optional/nullable properties are left out of the list.

```yaml
# path file
parameters:
  - name: status
    in: query
    required: false
    schema:
      $ref: './schema-conference-status.yml'
requestBody:
  required: true
  content:
    application/json:
      schema:
        $ref: './schema-conference-register-request.yml'
```

```yaml
# schema file
type: object
title: ConferenceRegisterRequestV1
required:
  - name
  - startDate
properties:
  name:
    type: string
  startDate:
    type: string
    format: date
  description:
    type: string   # optional → not in required
```

```yaml
post:
  operationId: registerConferenceV1
  tags:
    - Conferences V1
  summary: 컨퍼런스 등록
  description: >
    관리자 권한(`ROLE_MANAGER`) 이상 필요. 신규 컨퍼런스가 시스템에 등록된다.
  security:
    - keyflow-auth:
        - openid
        - profile
  requestBody:
    content:
      application/json:
        schema:
          $ref: './schema-conference-register-request.yml'
  responses:
    "201":
      description: "Successful Response"
      content:
        application/json:
          schema:
            $ref: "./schema-conference-response.yml"
```

## Standard Responses

- **Success:** `200` (get/put/transition) or `201` (register) returning the response schema via `$ref`.
- **Paged success:** a Spring Data `Page`-shaped object — `page` (shared Pageable) + `content` (array
  of the response schema):

  ```yaml
  "200":
    content:
      application/json:
        schema:
          type: object
          description: Spring Data Page 응답
          properties:
            page:
              $ref: "../../schema-paged-model.yml"
            content:
              type: array
              items:
                $ref: "./schema-conference-response.yml"
  ```

- **Errors:** `400` / `401` / `403` / `404` (whichever the operation can return) use
  `application/problem+json`, `$ref` the shared error schema, and include a **filled `example`** whose
  `code` / `exception` match the domain's error codes (see [exception-handling](exception-handling.md)):

  ```yaml
  "400":
    description: "Bad Request"
    content:
      application/problem+json:
        schema:
          $ref: "../../schema-error-response.yml"
        example:
          type: "about:blank"
          title: "Invalid session chair status transition."
          status: 400
          detail: "Invalid session chair status transition. from=REGISTERED, target=ACCEPTED"
          instance: "/api/v1/sessions/:sessionId/chairs/:id/accept"
          code: INVALID_SESSION_CHAIR_STATUS
          timestamp: "2026-04-27T10:00:00"
          exception: InvalidSessionChairStatusException
  ```

- **Idempotent transitions** (e.g. an already-`ACCEPTED` resource) are documented as a no-op `200`,
  not an error.

## Shared Schemas

Two schemas are global and **must never be duplicated per context**:

- `schema-error-response.yml` — RFC 9457 `ProblemDetail` (`type`, `title`, `status`, `detail`,
  `instance`, `code`, `timestamp`, `exception`). Used by every error response.
- `schema-paged-model.yml` — `Pageable` (`size`, `number`, `totalElements`, `totalPages`). Used by
  every paged response.

## Language

- Descriptions and summaries may be Korean.
- Keys, schema `title`s, enum values, and error `code`s are English.
