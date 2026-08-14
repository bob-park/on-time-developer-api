---
title: Pull Requests
scope: "**"
applies_to: opening a PR or titling one
related:
  - ./branching.md
  - ./versioning.md
  - ./commit-messages.md
---

# Pull Requests

> PR base branch + PR title rules (the title drives the version auto-bump). Read when opening a PR or titling one.

## Base Branch

`develop` is the default base branch. Only `hotfix/**` and the release-promotion PR target `master` directly.

| Source branch | PR base |
| ------------- | ------- |
| `feature/**` + day-to-day work (`chore/**`, `refactor/**`, `docs/**`, …) | `develop` |
| `hotfix/**` | `master` |
| release promotion (`develop` → `master`) | `master` |

- A `feature/**` PR (and any other day-to-day branch) **MUST** target `develop`; targeting `master` directly is not allowed. Promotion to `master` happens through a separate `develop` → `master` release PR.
- A `hotfix/**` PR targets `master`, then the fix is back-merged into `develop`.

## Title

A PR title is a short, descriptive summary in imperative mood. **Do not** prefix it with the
`feat:` / `fix:` / `docs:` commit-message prefix — that prefix belongs on commits (see
[commit-messages](commit-messages.md)), not on PR titles.

The title drives the automatic version bump on merge (see [versioning](versioning.md) for the
version pattern):

| PR title contains | Effect |
| ----------------- | ------ |
| `[major]` | `major` += 1 |
| `[minor]` | `minor` += 1 |
| (anything else) | `patch` += 1; on same day, `rc[index]` += 1 instead |

Examples:

```text
add asset upload endpoint
[minor] support multi-file asset upload
[major] migrate authentication to keyflow-auth
```
