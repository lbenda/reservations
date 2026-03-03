# F-017: Audit Log (Historie změn)

- Type: Feature
- Status: Todo
- Source: Product baseline (compliance & debugging)

## Description
Record important actions (booking changes, payment actions, login events, permission changes) into an audit log.

## Background
Audit trails help with disputes, debugging, and compliance.

Current implementation gaps:
1. No event log storage
2. No standardized event schema

## Scope
- AuditEvent entity: actor, action, entityType, entityId, metadata, timestamp
- Capture: create/update/cancel booking, block creation, payment status changes, role changes
- Admin UI to view/filter events
- Retention policy config

## Success Criteria
- Critical actions are recorded consistently
- Events are searchable by booking/client
- Log entries are immutable

## Related Tasks
- T-160: Audit schema + storage
- T-161: Hooks in booking/payment flows
- T-162: Admin UI - audit viewer

## Related Documentation
- docs/architecture/audit.md (Planned)