---
title: Logging
scope: src/main/java/**/*.java
applies_to: adding logging
related:
  - ./lombok.md
---

# Logging

> SLF4J via `@Slf4j`, parameterized messages. Read when adding logging.

- SLF4J only, via Lombok's `@Slf4j`.
- Use parameterized messages: `log.debug("s3 object meta. ({})", result);` — never string concatenation.
