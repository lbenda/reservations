# T-011: Service Validation Rules

- Type: Task
- Status: Todo
- Feature: F-001
- Owner: backend
- Related modules: services, domain
- Depends on: T-002

## Goal
Define validation rules for the Service entity in application code.

## Scope
- Validate:
  - `durationMin > 0`
  - `bufferBeforeMin >= 0`
  - `bufferAfterMin >= 0`
  - `minAdvanceMin >= 0`
  - `maxAdvanceDays >= 0`
  - `price >= 0` if present
- Define where validation is enforced (domain/service layer)

## Definition of Done
- Validation rules implemented in the chosen layer
- Repository rejects invalid data via validation entry point
