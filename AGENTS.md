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

## TODO Discipline
When implementing a task, check for existing `TODO [T-XXX]` references that point to that task or obviously related work.

If your task resolves the underlying issue:
1. update the implementation,
2. remove or update the resolved `TODO`,
3. mention it in your summary if it affected scope.

When adding a new `TODO` reference:
- use `TODO [T-XXX]` if the concrete task already exists,
- use `TODO [F-XXX]` if the follow-up is only known at feature level and task breakdown is not stable yet.
