# Project Context

This document is a compact structural map of the project.
It describes what exists, where it is, and how the system is shaped today.
It does NOT contain architectural rationale or history (see docs/adr/).

---

## Technology stack
- Server: Kotlin (Ktor)
- Web UI: React + TypeScript (Vite)

---

## High-level architecture
The system consists of a server backend and a web frontend.
Business logic is isolated from transport and presentation layers.

---

## Modules

### server
- REST API exposing reservation capabilities
- OpenAPI is generated code-first
- HTTP API documentation:
  - docs/API.md — human-readable API reference
  - docs/api-tests.http — executable API examples

### Module Structure

```
server/
  src/main/kotlin/cz/lbenda/reservation/
    Application.kt
```

### ui/web
- React-based web UI
- Separate frontend build (npm + Vite, not part of Gradle build)
- Communicates with the server exclusively via REST API
- Written in TypeScript

### Module Structure

```
ui/web/
  package.json
  vite.config.ts
  tsconfig.json
  index.html
  src/
    main.tsx
    App.tsx
```

---

## API boundaries
- Kotlin interfaces are used as explicit API boundaries:
  - between modules
  - for public services and entry points
- Interfaces act as stable contracts and context compression for AI tooling
- Internal helpers and local logic do not require interfaces

---

## Data & control flow

Typical flow:
HTTP → server → domain logic → response

---

## Work tracking

Work items are tracked in Markdown files under `/work/`:

```
work/
  features/   F-XXX-*.md   High-level feature definitions
  tasks/      T-XXX-*.md   Implementation tasks (split from features)
  bugs/       B-XXX-*.md   Bug reports and fixes
```

- Features describe *what* the system should do
- Tasks describe *how* to implement features
- Bugs describe defects in existing functionality

See `docs/TASKS_WORKFLOW.md` for detailed workflow and file structure.

---
