# A-002: React as the Web UI Technology

- Status: Accepted
- Date: 2026-03-03

## Decision
Implement the web user interface in React.

## Context
React enables rapid iteration on UI and has strong ecosystemx support. The project already anticipates a Redux-style state model, which aligns naturally with typical React state management patterns and predictable rendering.

## Consequences

Positive
- Fast UI development and iteration.
- Natural fit with Redux-like unidirectional data flow.
- Easy to build tooling around debugging and replay.

Trade-offs
- The UI and engine will likely be separated by a contract (API/events/state schema) unless engine code is also available in the JS runtime.
