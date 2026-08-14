---
title: Import Order
scope: src/main/java/**/*.java
applies_to: writing or reordering imports
related:
  - ./annotation-order.md
---

# Import Order

> Import group ordering. Read when writing or reordering imports.

Groups separated by a single blank line, in this order:

1. `java.*`, `javax.*`, `jakarta.*`
2. `lombok.*`
3. `org.springframework.*`
4. Third-party (`org.apache.*`, `com.google.*`, `software.amazon.awssdk.*`, `org.jspecify.*`, `io.github.*`, …)
5. `com.malgn.*`

Static imports go last, in their own group.
