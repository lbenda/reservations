# T-022: DST and Boundary Tests

- Type: Task
- Status: Done
- Feature: F-003
- Owner: backend

## Goal
Cover edge cases for timezone-sensitive slot generation.

## Scope
- Test DST transitions in supported business timezones
- Test boundary conditions around opening, closing, breaks, and buffers
- Verify edge cases at date-range boundaries and near midnight

## Definition of Done
- Availability tests cover DST transitions and boundary behavior
- Edge-case regressions are reproducible from focused tests
- Timezone-sensitive behavior is documented by test names and fixtures
