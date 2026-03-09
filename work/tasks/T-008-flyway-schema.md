# T-008: Flyway Schema Baseline

- Type: Task
- Status: Done
- Feature: F-001
- Owner: backend

## Goal
Define the initial database schema for the reservation system via Flyway.

## Scope
- Add Flyway migrations for tenant, catalog, clients, payments, packages, audit, integrations, booking
- Keep schema aligned with LDM

## Definition of Done
- Migrations exist under `server/src/main/resources/db/migration`
- Schema covers all core entities and relationships
