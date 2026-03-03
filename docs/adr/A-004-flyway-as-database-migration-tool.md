# A-004: Flyway as Database Migration Tool

- Status: Accepted
- Date: 2026-03-03

## Decision
Use Flyway as the database schema migration tool for the reservation application.

Migrations will be maintained as SQL-first, versioned scripts in the repository and will be executed:

- on application startup in development environments (as appropriate),
- during CI pipelines against a fresh PostgreSQL instance,
- in automated tests when bootstrapping a clean database/schema is required.

The migration workflow will be forward-only: once a migration is merged, it is immutable; changes are applied via new migrations rather than editing historical ones.

## Context
The application is PostgreSQL-first and correctness depends on real database behavior (transactions, constraints, concurrency). The team also needs a fast and predictable developer workflow, especially during iterative debugging where tests are re-run frequently.

Flyway provides:

- a clear, linear migration history via versioned scripts (easy to reason about “what version is this DB on?”),
- minimal framework overhead with SQL-first migrations,
- strong fit with PostgreSQL-focused development and CI pipelines.

Liquibase offers multi-DB abstractions and built-in rollback workflows, but it adds more operational and cognitive overhead, and its “current database version” is less directly represented than Flyway’s versioned migration model. For this project, multi-DB support is not a primary requirement and the operational preference is predictability and fast feedback loops.

## Consequences

Positive

- Clear “database version” model aligned with versioned migration scripts.
- SQL-first migrations match PostgreSQL as the target platform and reduce translation/magic layers.
- Easier review and auditing of schema changes in PRs (diffable SQL scripts).
- Encourages production-realistic testing by keeping schema evolution deterministic.

Trade-offs

- Rollbacks are not the default workflow; reversals are handled by new “fix-forward” migrations and/or operational recovery (e.g., backups) when necessary.
- Requires discipline: merged migrations must be treated as immutable to avoid environment drift.
- Some cross-database portability is forfeited in favor of PostgreSQL-first capabilities and simplicity.

Implementation notes

- Store migrations under a standard path (e.g., db/migration) and use a consistent naming convention (e.g., V2026_03_03_1201__add_reservation_status.sql).
- CI must validate “fresh DB → migrate → tests” to guarantee migrations build a working schema from scratch.
- For fast local iteration, tests should avoid re-running migrations repeatedly:
  - Run migrations once per test run (or once per reused DB instance),
  - Prefer transactional rollback or schema-based isolation for test cleanliness rather than rebuilding the schema each time.
