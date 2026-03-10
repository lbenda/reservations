# T-003: Admin API – Service CRUD

- Parent: F-001
- Type: Task
- Status: In review
- Feature: F-001
- Owner: backend
- Related modules: api, auth
- Depends on: T-002

## Goal
Expose authenticated endpoints to create, update, archive and list services for a business.

## Scope
- Endpoints (example):
    - `POST /api/admin/services`
    - `GET /api/admin/services`
    - `GET /api/admin/services/:id`
    - `PATCH /api/admin/services/:id`
    - `POST /api/admin/services/:id/archive` (or PATCH `isActive=false`)
- Request validation & error format:
    - 400 for invalid payload
    - 404 if service not found within business scope
- Business scoping:
    - Services must be filtered by `businessId` from auth context
- Sorting/filtering:
    - Default sort by `name`
    - Optional filter `isActive`

## Out of Scope
- Public booking endpoints (F-004)
- RBAC matrix (F-016) beyond “admin-only”

## Definition of Done
- OpenAPI/Swagger (or equivalent) updated
- All endpoints covered by integration tests
- Archiving prevents service appearing in “active services” list
