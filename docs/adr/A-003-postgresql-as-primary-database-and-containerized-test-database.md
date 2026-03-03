# A-003: PostgreSQL as Primary Database and Containerized Test Database

- Status: Accepted
- Date: 2026-03-03

## Decision
Use PostgreSQL as the primary relational database for the reservation application.

Run database-backed automated tests against a real PostgreSQL instance, preferably provisioned via a Docker container:
- Locally: tests default to starting PostgreSQL via Testcontainers.
- CI (GitHub Actions): tests run with PostgreSQL available via either Testcontainers or a GitHub Actions service container (Docker-based), with the default path being Docker-backed PostgreSQL.

Additionally, the test suite must support running against an already running PostgreSQL instance (e.g., developer-provided or company-provided DB) via configuration, without requiring Docker.

Provide a Gradle-driven developer experience that can start/stop PostgreSQL for local development/testing (e.g., via Docker Compose integration or a Gradle task), so developers do not need to install PostgreSQL manually.

## Context
The application’s domain is reservation/availability and is CRUD-heavy with correctness requirements that depend on database behavior (transactions, constraints, concurrency and race conditions). Using PostgreSQL as the primary database ensures:
- consistent behavior between development, test, and production,
- ability to enforce data integrity and prevent double-booking at the database level,
- strong transactional semantics needed for reservation flows.

Embedded in-memory databases (H2/HSQLDB/Derby) provide fast startup but differ materially from PostgreSQL in SQL dialect, locking, isolation, constraint enforcement, and edge-case behaviors. These differences can cause false confidence in tests and production-only failures—especially under concurrency.

Testcontainers provides a reliable way to run real PostgreSQL locally and in CI with minimal setup, but some environments (e.g., locked-down corporate machines) may restrict Docker usage. Therefore, the test strategy must allow switching to an existing PostgreSQL instance through configuration.

## Consequences

Positive

- Production and test environments use the same database engine, reducing “works on H2, fails on Postgres” issues.
- Correctness-critical behaviors (constraints, locking, isolation, concurrent reservation attempts) can be validated realistically.
- Minimal developer setup when Docker is available: tests self-provision PostgreSQL.
- Works well in GitHub Actions where Docker/service containers are first-class, enabling consistent CI runs.
- Flexible fallback: tests can run against a pre-provisioned PostgreSQL instance when Docker is unavailable.
* Gradle tasks can standardize “start DB / run tests / stop DB” workflows across the team.

Trade-offs

- Docker dependency for the default local test path (may be blocked on some corporate machines).
- Container startup cost exists, but can be mitigated by:
  - starting PostgreSQL once per test run,
  - using transactional rollback or schema-based resets in tests,
  - optionally reusing containers locally.
- Requires maintaining configuration for two modes of operation:
  - “managed by tests” (Testcontainers / Docker)
  - “external DB” (pre-running PostgreSQL)
- CI configuration must ensure PostgreSQL availability and readiness (health checks/timeouts), though this is straightforward in GitHub Actions.

Implementation notes
- Tests should read DB connection settings from a single configuration source (e.g., environment variables / Gradle properties) and select mode:
  - External mode: TEST_DB_URL, TEST_DB_USER, TEST_DB_PASS provided → tests use that DB.
  - Container mode (default): if external mode not set → use Testcontainers to provision PostgreSQL.
- Provide Gradle tasks for developer convenience, e.g.:
  - dbUp / dbDown (Docker Compose-based) for local persistent Postgres,
  - test continues to work without installing PostgreSQL (via Testcontainers when Docker is allowed),
  - documentation for “external DB mode” for Docker-restricted environments.
- Prefer fast reset strategies for DB-backed tests (transaction rollback where possible; otherwise schema-per-test-class) to keep the feedback loop tight during iterative debugging.
