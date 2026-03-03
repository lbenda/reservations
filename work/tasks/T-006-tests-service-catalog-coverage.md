# T-006: Tests – Service Catalog Coverage

- Parent: F-001
- Status: Todo
- Owner: qa
- Related modules: backend, frontend
- Depends on: T-002, T-003, T-004

## Goal
Ensure Service catalog is stable: validations, CRUD behavior, and archive semantics are covered by tests.

## Scope
- Backend unit tests:
    - validation rules (duration, buffers, price, advance constraints)
- Backend integration tests:
    - CRUD happy path
    - business scoping (cannot access another business’s services)
    - archive behavior
- Frontend tests:
    - form validation and error rendering
    - archive flow UI behavior

## Out of Scope
- Availability engine correctness (F-003 tests)
- Notifications (F-007)

## Definition of Done
- Test suite runs green in CI
- Coverage includes at least:
    - 1 negative test per validation rule
    - 1 scoping/security regression test
    - 1 UI flow test (create/edit/archive)