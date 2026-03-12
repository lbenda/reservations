# T-021: Slot Generation

- Type: Task
- Status: Done
- Feature: F-003
- Owner: backend

## Goal
Generate available slots from schedule input and conflict rules.

## Scope
- Traverse requested date ranges and candidate start times
- Combine service duration, slot interval, and optional staff filtering
- Respect working ranges, breaks, and exceptions from the schedule input service
- Return stable slot output with start time, end time, and staff assignment

## Definition of Done
- Slot generation returns only valid slots inside allowed schedule windows
- Returned slots exclude ranges blocked by conflicts and breaks
- Same query returns deterministic output ordering
