# A-008: jOOQ as Database Access Layer

- Status: Accepted
- Date: 2026-03-03

## Decision
Use **jOOQ** as the primary database access layer for PostgreSQL.

The application will:
- maintain schema evolution with **Flyway** migrations,
- generate jOOQ code from the database schema (or migration-derived schema) as part of the build,
- implement persistence in repositories using jOOQ’s DSL and explicit SQL semantics.

## Context
The application is PostgreSQL-first and CRUD-heavy, with correctness requirements that depend on:
- reliable transactional behavior,
- clear concurrency and locking semantics,
- predictable query performance.

ORM approaches (e.g., JPA/Hibernate) can introduce implicit behaviors (lazy loading, flush timing, cascade rules, N+1 queries) that complicate debugging and can obscure the actual SQL executed—especially problematic for rapid iteration and AI-assisted changes. jOOQ provides:
- explicit, type-safe SQL construction,
- excellent support for PostgreSQL features and idioms,
- predictable behavior and easy inspection of generated SQL.

## Consequences

Positive
- High control and visibility of executed SQL; easier debugging and performance tuning.
- Type-safe query construction with strong IDE support.
- Natural fit for PostgreSQL-specific features (upserts, locking, complex filtering, reporting queries).
- Encourages explicit transaction boundaries and correctness-oriented DB usage (constraints, locking patterns).

Trade-offs
- More “SQL-shaped” development than ORM; developers must design queries and mapping consciously.
- Code generation adds build complexity (codegen configuration, regeneration cadence).
- Domain modeling is not “entity-first”; object graphs and relationship navigation must be modeled explicitly at the service layer.

Implementation notes
- Prefer a layered structure:
    - repositories implement SQL via jOOQ (and remain free of HTTP concerns),
    - services define transaction boundaries and enforce invariants (e.g., no double booking),
    - routes/controllers handle HTTP and DTO mapping.
- Generate jOOQ classes as part of the build to keep schema and queries aligned.
- Log SQL (in non-prod and test) at a level that supports debugging without excessive noise.
- For test speed:
    - run migrations once per test run (or once per reused DB instance),
    - isolate tests via transactional rollback where possible,
    - for tests requiring commits, isolate via schema-per-test-class or targeted cleanup.
