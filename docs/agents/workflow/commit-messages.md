---
title: Commit Message Convention
scope: "**"
applies_to: writing a commit message
related:
  - ./branching.md
---

# Commit Message Convention

> Commit message prefixes and format. Read when writing a commit message.

Format: `<prefix>: <subject>` — one line, imperative mood, no trailing period required.

Allowed prefixes:

| Prefix | Use for |
| ------ | ------- |
| `feat` | New feature. |
| `fix` | Bug or issue fix on existing functionality. |
| `refactor` | Code restructuring with no behavior change (rename, extract, reorganize). |
| `build` | Build tooling and dependency changes (Gradle, Docker, CI config). |
| `docs` | Documentation or comment-only changes. |
| `test` | Adding or modifying tests. |

Examples:

```text
feat: add asset upload endpoint
fix: correct s3 head-object null handling
refactor: extract JwtRoleGrantAuthoritiesConverter
build: bump malgn-spring-boot-starter to 2.0.11
docs: document builder pattern convention in AGENTS.md
test: add AssetJpaRepository slice tests
```

An optional body, separated by a blank line, explains *why* the change was made when the diff alone doesn't make it obvious. Body lines wrap at ~72 characters.
