# T-025: Booking MCP Tools

- Type: Task
- Status: Backlog
- Feature: F-021
- Owner: backend

## Goal
Expose booking-oriented MCP tools after the read-only surface is stable.

## Scope
- Add tools for booking creation using existing availability and validation rules
- Add tools for booking lookup and cancellation/reschedule entry points as appropriate
- Reuse idempotency, validation, and domain constraints from the booking flow
- Ensure write operations return explicit success/failure contracts

## Definition of Done
- MCP booking tools cannot create bookings that bypass existing rules
- Write operations are covered by integration tests
- Tool responses contain enough information for agent follow-up actions
