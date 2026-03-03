# F-013: Cancellation & Reschedule Links (Samoobsluha klienta)

- Type: Feature
- Status: Todo
- Source: Product baseline (client UX)

## Description
Provide secure customer links to cancel or reschedule bookings within policy limits.

## Background
Reducing admin workload and improving customer convenience.

Current implementation gaps:
1. No signed link mechanism
2. No policy enforcement

## Scope
- Signed token link included in notifications
- Policy: minAdvanceMin for cancel/reschedule
- Reschedule flow uses availability engine
- Audit trail of customer actions

## Success Criteria
- Customer can cancel/reschedule without login
- Links expire or are single-use (configurable)
- Policy limits are enforced reliably

## Related Tasks
- T-120: Token signing + validation
- T-121: Public cancel/reschedule endpoints
- T-122: Audit events

## Related Documentation
- docs/security/signed-links.md (Planned)
