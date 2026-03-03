# A-001: Kotlin as the Core Implementation Language

- Status: Accepted
- Date: 2026-03-03

## Decision
Implement the core application (including the game engine) in Kotlin.

## Context
Kotlin provides strong interoperability with the JVM ecosystem and is a first-class language for Android. It offers good language ergonomics (null-safety, data classes, sealed types), which are useful for modeling game state, rules, and immutable updates.

## Consequences

Positive
- Strong Android alignment and tooling.
- Expressive modeling for state machines and domain rules.
- Testability improves with pure functions and data modeling patterns.

Trade-offs
- If the web UI needs to share engine code directly, Kotlin may require a cross-platform approach (e.g., Kotlin Multiplatform) or an API boundary instead of code sharing.
- Some contributors may be more familiar with JavaScript/TypeScript than Kotlin.
