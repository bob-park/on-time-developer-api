---
title: Branch Naming
scope: "**"
applies_to: starting a branch
related:
  - ./pr.md
  - ./versioning.md
  - ./commit-messages.md
---

# Branch Naming

> Branch naming rules. Read when starting a branch.

| Branch | Purpose |
| ------ | ------- |
| `master` | Production-ready code. Release tags are cut from this branch (see [versioning](versioning.md)). |
| `develop` | Integration branch for the next release. Feature branches merge here first. |
| `feature/<topic>` | Active feature development. Branch from `develop`, merge back to `develop` via PR. |
| `hotfix/<topic>` | Urgent fixes against a released version. Branch from `master`, merge into both `master` and `develop`. |

Rules:

- Use lowercase, kebab-case for `<topic>` (e.g., `feature/asset-upload`, `hotfix/jwt-scope-mapping`).
- Do not commit directly to `master` or `develop` — always go through a PR. PR base-branch rules live in [pr](pr.md#base-branch).
- A tag is created on `master` when a release ships; tag name follows the version pattern in [versioning](versioning.md).
