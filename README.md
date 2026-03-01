# Reservation

A reservation system with a Kotlin backend and React web frontend.

---

## Architecture Overview

The system consists of a server (Kotlin/Ktor) and a web UI (React/TypeScript/Vite).
Business logic lives in the server, the web UI communicates via REST API.

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

---

## GitHub Actions

Tento repozitar pouziva **GitHub Actions** k synchronizaci:
- Markdown ticketu v repozitari
- GitHub Issues
- GitHub Projects (v2)

Repo je **zdrojem pravdy**.
Issues a Project slouzi pouze jako stavova projekce.
