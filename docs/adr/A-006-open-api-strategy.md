# A-006: OpenAPI strategy

- Status: Accepted
- Date: 2026-02-02

## Decision
We adopt a code-first OpenAPI approach during early development:
- API is primarily defined by Kotlin controller interfaces and implementations.
- OpenAPI specification is generated from code.
- docs/API.md serves as the human-readable API description.
- api-test.http contains executable examples of API usage.

When the API becomes stable or gains external consumers:
- OpenAPI specification may become the primary contract.
- Versioning rules will be enforced.

## Context
The server module exposes a REST API used by multiple clients (web UI, external tools).
During early development, the API shape is expected to evolve.
Maintaining a strict contract-first OpenAPI specification upfront would slow down iteration.

## Consequences
- Faster iteration during early development.
- API documentation stays close to implementation.
- docs/API.md and api-test.http must be updated with every API change.
- Transition to contract-first OpenAPI is possible without major refactoring.
