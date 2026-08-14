# CLAUDE.md

@AGENTS.md

## Claude Code 전용 규칙

### Plan Mode

- 큰 변경(파일 3개 이상 / 새 기능 / 마이그레이션) 은 plan mode 로 진행한다.

### Skill Usage

- 가능하면 `superpowers` 계열 스킬을 우선 사용한다.
- 새 기능 / 리팩토링: `superpowers:brainstorming` → `superpowers:writing-plans` → `superpowers:executing-plans` 흐름.
- 디버깅: `superpowers:systematic-debugging`.
- 완료 직전 검증: `superpowers:verification-before-completion`.

### Language

- 기술 본문(설명, 코드 주석, 식별자) 은 영어.
- 도메인 / 비지니스 맥락 설명은 한국어 허용 (특히 `docs/domain/glossary.md` 의 한국어 용어를 인용할 때).

### Convention Enforcement

- 코드 변경 시 `docs/agents/conventions/` 의 규칙(annotation order / import order / builder pattern / lombok / naming / null·validation / logging / web-api / jpa-entity / jpa-persistence-adapter / exception-handling / application-service / domain-model-behavior / api-docs / modulith-boundaries / domain-events / web-view)을 반드시 따른다.
- 브랜치·커밋 규칙은 `docs/agents/workflow/` (branching / commit-messages / pr / versioning) 를 반드시 따른다.
- 위반된 기존 코드를 발견하면 같은 PR 에서 고치지 말고 별도 메모로 남긴다 (scope creep 방지).

### Dependency Changes

- `build.gradle` 의 의존성 추가 / 제거 / 버전 변경은 반드시 사용자 확인 후 진행한다.

### Destructive Operations

- `git reset --hard`, `git push --force`, 파일 대량 삭제, 브랜치 삭제 등은 사용자 명시 승인 전 절대 실행 금지.
