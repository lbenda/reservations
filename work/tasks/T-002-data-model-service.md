# T-002: Data Model – Service

- Parent: F-001
- Status: Todo
- Owner: backend
- Related modules: services, database
- Depends on: -

## Goal
Define and persist the core Service entity used across booking flow and availability calculations.

## Scope
- Create `Service` table/collection with fields:
    - `id` (uuid)
    - `businessId`
    - `name`
    - `description` (optional)
    - `durationMin`
    - `price` (decimal, optional)
    - `currency` (ISO-4217, default from business)
    - `bufferBeforeMin` (default 0)
    - `bufferAfterMin` (default 0)
    - `minAdvanceMin` (default 0)
    - `maxAdvanceDays` (default e.g. 365)
    - `isActive` (default true)
    - `createdAt`, `updatedAt`
- Constraints & validation:
    - `durationMin > 0`
    - buffers >= 0
    - `minAdvanceMin >= 0`
    - `maxAdvanceDays >= 0`
    - `price >= 0` if present
- Soft-delete or archive behavior via `isActive=false` (no hard delete for now)
- Seed one example service in dev environment (optional)

## Out of Scope
- Staff-service assignment (F-002)
- Availability engine logic (F-003)
- Payments (F-008)

## Definition of Done
- Migration applied and reversible
- ORM/model layer added with validations
- Basic repository/service methods:
    - create/update/get/list (scoped by businessId)
- Unit tests for validation rules