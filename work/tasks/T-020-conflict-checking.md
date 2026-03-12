# T-020: Conflict Checking

- Type: Task
- Status: In review
- Feature: F-003
- Owner: backend

## Goal
Implement deterministic conflict detection for availability calculations.

## Scope
- Detect overlap with existing bookings for a staff member
- Apply service buffers before and after occupied booking time
- Consider manual blocks and day-off style schedule exclusions as unavailable time
- Keep the overlap logic reusable by later slot generation

## Definition of Done
- Availability code can answer whether a candidate time range conflicts with existing occupied time
- Buffer handling is explicit and covered by tests
- Conflict rules are deterministic for the same input data
