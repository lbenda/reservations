# T-016: Data Model - Staff Schedules and Exceptions

- Type: Task
- Status: In review
- Feature: F-002
- Owner: backend

## Goal
Add persistent data structures for per-staff weekly working hours, breaks, and schedule exceptions.

## Scope
- Add schema for weekly staff time ranges
- Add schema for staff schedule exceptions
- Add Kotlin models and repository access
- Add CRUD tests for the new persistence layer

## Definition of Done
- Weekly schedules can be stored per staff member
- Breaks can be represented in weekly schedules and exceptions
- Exceptions support day off and special opening hours
- CRUD tests cover the new repositories
