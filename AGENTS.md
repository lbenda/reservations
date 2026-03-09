# Agent Instructions (Codex)

This project also defines shared agent rules in `CLAUDE.md`.

You MUST read and follow `CLAUDE.md` as part of your system instructions.
If there is any conflict:
- CLAUDE.md defines shared project conventions

Do not ignore or partially apply `CLAUDE.md`.

## jOOQ Codegen Workflow
Database schema is defined in Flyway migrations. jOOQ Kotlin table classes are generated, not hand-written.

After any schema changes:
1. Run `./gradlew :server:generateJooq`
2. Use generated classes from `cz.lbenda.reservation.jooq.tables.*`

Do not manually edit generated sources in `server/build/generated-src/jooq`.
