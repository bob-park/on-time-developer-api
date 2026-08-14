---
title: Configuration & Secrets
scope: ["src/main/resources/**", "src/main/java/**/config/**"]
applies_to: adding config or secrets
related:
  - ./security.md
---

# Configuration & Secrets

> `application*.yml`, secrets sourcing, dev-profile gitignore. Read when adding config or secrets.

- App config in `application*.yml`. Dev-profile files (`application*dev.yml`) and `docker-compose*dev.yml` are git-ignored (see `.gitignore`).
- AWS S3 bucket name comes from `${app.aws.s3.bucket-name}`.
- Never commit credentials. Source them from environment variables or Spring Cloud Consul config.
