# F-003: Availability Engine

- Type: Feature
- Status: Todo
- Source: Product baseline (core scheduling)

## Description
Implement time-slot generation that returns available slots for a given service and staff (optional),
considering working hours, breaks, buffers, existing bookings and exceptions.

## Background
The availability engine is the heart of the system. Everything else relies on correct slot computation.

Current implementation gaps:
1. No unified availability algorithm
2. No conflict detection

## Scope
- Input: date range, serviceId, optional staffId, timezone
- Consider: working hours, exceptions, breaks
- Consider: existing bookings (hard block)
- Apply buffers (before/after) and service duration
- Slot interval config (e.g., 5/10/15 min)
- Return slots with startTime, endTime, staffId

## Success Criteria
- No returned slot overlaps any booking/buffer
- Slots respect schedules and exceptions
- Same query is deterministic for same data
- Covered by tests (edge cases: DST, buffers, breaks)

## Related Tasks
- T-020: Implement conflict checking
- T-021: Implement slot generation
- T-022: Tests - DST & boundaries

## Related Documentation
- docs/architecture/availability.md (Planned)