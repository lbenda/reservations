# F-005: Booking Management

- Type: Feature
- Status: Todo
- Source: Product baseline (calendar ops)

## Description
Provide an admin calendar to view, create, edit, move and cancel bookings. Includes day/week views and staff filtering.

## Background
Operators need operational control: reschedule calls, handle no-shows, block time.

Current implementation gaps:
1. No operator interface
2. No edit/reschedule rules

## Scope
- Calendar UI: day/week, staff lanes, service labels
- Actions: create internal booking, reschedule (drag/drop optional), cancel, mark no-show
- Notes field for internal use
- Permissions (requires authenticated admin)

## Success Criteria
- Admin can reschedule without creating conflicts
- Cancel/reschedule updates notifications (if enabled)
- Calendar loads fast for a week range

## Related Tasks
- T-040: Admin calendar UI
- T-041: Booking update API
- T-042: Permissions guard

## Related Documentation
- docs/ui/admin-calendar.md (Planned)