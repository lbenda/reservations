# T-002: Data Model – Service

- Type: Task
- Parent: F-001
- Status: Done
- Feature: F-001
- Owner: backend
- Related modules: services, database
- Depends on: -

## Goal
Define and persist the core Service entity schema and repository.

## Scope
- Create `Service` table with full field set and defaults
- Repository CRUD for Service (scoped by businessId)

## Out of Scope
- Validation rules (see T-011)
- Validation unit tests (see T-012)
- Integration tests for CRUD/scoping (see T-014)

## Definition of Done
- Migration applied and reversible
- Repository methods exist for create/update/get/list scoped by businessId
