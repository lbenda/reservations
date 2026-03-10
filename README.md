# Reservation

A reservation system with a Kotlin backend and React web frontend.

---

## Architecture Overview

The system consists of a server (Kotlin/Ktor) and a web UI (React/TypeScript/Vite).
Business logic lives in the server, the web UI communicates via REST API.

Database is PostgreSQL with jOOQ as the database access layer.

---

## Project Structure

```
.
├── server/                 # REST API server (Ktor)
├── ui/web/                 # React web UI
├── docs/
│   ├── adr/                # Architectural Decision Records
│   ├── API.md              # REST API reference
│   ├── api-tests.http      # API test examples
│   └── TASK_WORKFLOW.md    # Definition of workflow
├── work/
│   ├── features/           # Feature definitions
│   ├── tasks/              # Implementation tasks
│   └── bugs/               # Bug reports and fixes
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Documentation

- [REST API](docs/API.md) - Server API reference

---

## Running the Server

```bash
./gradlew :server:run
```

Server starts on http://localhost:8080

## Generate jOOQ Sources

Database schema is managed by Flyway migrations. Generate jOOQ Kotlin tables after schema changes:

```bash
./gradlew :server:generateJooq
```

If Docker is not available, point codegen to an existing PostgreSQL instance:

```bash
export JOOQ_DB_URL=jdbc:postgresql://localhost:5432/your_db
export JOOQ_DB_USER=your_user
export JOOQ_DB_PASS=your_pass
./gradlew :server:generateJooq
```

## Running Web UI

```bash
cd ui/web
npm install
npm run dev
```

or

```bash
./gradlew :ui-web:dev
```

---

## Generate context for AI

```bash
./scripts/ai-briefing.sh F-007 > /tmp/AI_BRIEFING_F-007.md
```

```powershell
bash -c "./scripts/ai-briefing.sh F-007 > /tmp/AI_BRIEFING_F-007.md"
```

## Running Tests

By default, database-backed tests run using Testcontainers (Docker required).

```bash
./gradlew :server:test
```

To run against an existing PostgreSQL instance (no Docker), set:

```bash
export TEST_DB_URL=jdbc:postgresql://localhost:5432/your_db
export TEST_DB_USER=your_user
export TEST_DB_PASS=your_pass
./gradlew :server:test
```

---

## GitHub Actions

This repo use **GitHub Actions** for synchronisation:
- Markdown tickests in repository
- GitHub Issues
- GitHub Projects (v2)

Repo is **source of truth**.
Issues and Project is used only for view.
