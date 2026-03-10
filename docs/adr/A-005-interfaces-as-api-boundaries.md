# A-005: Interfaces as API boundaries

- Status: Accepted
- Date: 2026-02-02

## Decision
We use Kotlin interfaces as explicit API boundaries for:
- Cross-module and cross-package communication
- Public services and entry points
- Components frequently referenced by the LLM

Interfaces are used primarily as:
- API contracts
- Context compression for AI tooling

Interfaces are NOT introduced for polymorphism by default.

## Context
The project is developed with heavy assistance from an LLM (Claude).
LLMs work best with stable, compact API descriptions and struggle with large implementation details.
In Kotlin, interfaces provide a concise representation of behavior without exposing internal logic.

## Consequences
- Public behavior is clearly defined and stable.
- LLM interactions can rely on interfaces instead of full implementations, reducing context size.
- Implementations can change without impacting API consumers or AI context.
- Some interfaces may have a single implementation, which is acceptable.
- Internal helpers and local logic do not require interfaces.
