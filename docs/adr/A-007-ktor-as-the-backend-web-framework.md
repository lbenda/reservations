# A-007: Ktor as the Backend Web Framework

- Status: Accepted
- Date: 2026-03-03

## Decision
Implement the backend HTTP API using **Ktor**.

Ktor will be used as the primary web framework for routing, request/response handling, and middleware-style concerns (authentication, sessions, logging, metrics). The application will run on a Ktor-supported engine (e.g., Netty or CIO), selected based on operational requirements.

## Context
The project prioritizes:
- **fast startup** and low runtime overhead,
- **high code readability** and minimal framework “magic” (to support rapid iteration and AI-assisted development),
- explicit control over request lifecycle, authentication, and cross-cutting concerns.

While Spring Boot offers extensive auto-configuration and integrations, it introduces additional startup time and a larger implicit behavior surface area (DI scopes, proxies, AOP, ORM lifecycle). Ktor provides a smaller, Kotlin-native programming model centered on a request pipeline and composable plugins, which matches the desired engineering style.

## Consequences

Positive
- Faster startup and smaller framework surface area than full-stack Spring Boot setups.
- Clear, explicit request pipeline and middleware composition (plugins/interceptors).
- Reduced hidden behaviors (fewer proxies, less auto-configuration), improving debuggability and AI-agent friendliness.
- Modular architecture encourages explicit boundaries between routing, services, and persistence.

Trade-offs
- Fewer “batteries included” features compared to Spring Boot; integrations (auth, metrics, DI) require explicit selection and wiring.
- No built-in DI container with standard scopes (singleton/request/session); object lifetimes must be handled explicitly or via a chosen DI library.
- Cross-cutting concerns are implemented via Ktor plugins and explicit decorators rather than traditional Spring-style AOP.

Implementation notes
- Use Ktor plugins for request-level cross-cutting concerns (request logging, correlation IDs, auth, sessions, metrics).
- Prefer explicit service composition and decorator/wrapper patterns for service-level cross-cutting concerns (auditing, timing, business logging) rather than proxy-based AOP.
- Keep application modules explicit:
    - `routes/` for HTTP routing and DTO mapping
    - `services/` for business logic and transaction boundaries
    - `repositories/` for persistence operations
    - `plugins/` for Ktor plugin configuration (auth, sessions, logging, metrics)
