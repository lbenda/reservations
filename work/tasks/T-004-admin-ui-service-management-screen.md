# T-004: Admin UI – Service Management Screen

- Parent: F-001
- Type: Task
- Status: Done
- Feature: F-001
- Owner: frontend
- Related modules: admin-ui
- Depends on: T-003

## Goal
Allow an admin to manage services from a simple UI: list, create, edit, archive.

## Scope
- Service list view:
    - name, duration, price, active/inactive state
    - search by name
    - toggle “show archived”
- Create/Edit form:
    - name (required)
    - description
    - duration (required, minutes)
    - price + currency (optional if free)
    - buffers before/after
    - min advance / max advance
- Archive action:
    - confirmation modal
    - success/error toast

## Out of Scope
- Staff assignment UI (F-002)
- Public booking site branding (F-009)

## Definition of Done
- UI matches API validations (client-side + server-side errors displayed)
- Works on mobile viewport
- Basic E2E test: create -> edit -> archive -> filter archived
