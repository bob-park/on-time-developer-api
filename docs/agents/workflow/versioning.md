---
title: Versioning
scope: "**"
applies_to: cutting a release or reading the version string
related:
  - ./pr.md
  - ./branching.md
---

# Versioning

> Release version pattern + auto-bump mechanism. Read when cutting a release or reading the version string.

Version pattern: `[major].[minor].[patch]-rc[index]-[yyyyMMdd]`

The version is auto-bumped via GitHub Actions when a PR merges. The bump size is decided by the
PR title — see [pr](pr.md#title) for the title → bump mapping.
