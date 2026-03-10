# F-016: Roles & Permissions (RBAC)

- Type: Feature
- Status: Backlog
- Source: Product baseline (security)

## Description
Implement role-based access control for owners, managers and staff with least-privilege permissions.

## Background
Even a small clinic needs different access levels (front desk vs therapist vs owner).

Current implementation gaps:
1. No RBAC system
2. No permission checks on admin endpoints

## Scope
- Roles: Owner, Manager, Staff (initial)
- Permission matrix (CRUD services, view all clients, view own bookings, etc.)
- Middleware/guard checks
- UI hides unauthorized actions

## Success Criteria
- Unauthorized users cannot access restricted data/actions
- Staff can be limited to own bookings
- Audit logs record privileged actions (hook to F-017)

## Related Tasks
- T-150: RBAC model + guards
- T-151: Permission matrix definition
- T-152: UI authorization

## Related Documentation
- docs/security/rbac.md (Planned)
