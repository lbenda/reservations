# T-014: Service Integration Tests

- Type: Task
- Status: Done
- Feature: F-001
- Owner: qa
- Related modules: backend, tests
- Depends on: T-002, T-003

## Goal
Cover backend integration tests for Service CRUD and scoping.

## Scope
- CRUD happy path
- Business scoping (cannot access another business’s services)
- Archive behavior and filtering by `isActive`

## Definition of Done
- Integration tests run green in CI
- Scoping and archive behaviors covered
