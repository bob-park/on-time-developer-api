---
title: Build, Run & Test
scope: ["build.gradle", "src/test/**"]
applies_to: building, running, or packaging the app
related:
  - ./testing.md
---

# Build, Run & Test

> Gradle build/run/test commands + multi-arch Docker bake. Read when building, running, or packaging the app.

- `./gradlew build` — full build (compiles, runs tests, copies API docs).
- `./gradlew test` — JUnit 5 tests only (`useJUnitPlatform()`).
- `./gradlew bootRun` — local run.
- API docs (`docs/apis/*.yml`) are copied to `build/resources/main/static/v1/docs/apis` by the `copyApiDocs` task (chained from `processResources`). This is what makes `swagger-ui` integration possible after build.

## Docker (multi-arch)

```bash
VERSION=$(./gradlew properties -q | grep "^version:" | awk '{print $2}') \
  docker buildx bake -f docker-compose.yml --push --provenance false
```

Update `docker-compose.yml` to use the correct image and tag for the target environment before running the bake.
