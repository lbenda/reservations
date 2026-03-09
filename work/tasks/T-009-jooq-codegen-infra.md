# T-009: jOOQ Codegen Infrastructure

- Type: Task
- Status: Done
- Feature: F-001
- Owner: backend

## Goal
Enable repeatable jOOQ code generation from Flyway migrations.

## Scope
- Add codegen source set and dependencies
- Implement jOOQ codegen runner using Testcontainers + Flyway
- Add Gradle task to generate jOOQ Kotlin sources
- Document usage in README and AGENTS

## Definition of Done
- `:server:generateJooq` task runs and generates Kotlin sources
- Instructions exist for re-generating code after schema changes
