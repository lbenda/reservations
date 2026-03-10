# F-007: Automated Notifications (Email/SMS připomínky)

- Type: Feature
- Status: Backlog
- Source: Product baseline (reminders)

## Description
Send automated confirmations and reminders via email (and optional SMS) based on configurable templates and schedules.

## Background
Reminders reduce no-shows and improve trust.

Current implementation gaps:
1. No notification pipeline
2. No template system

## Scope
- Notification events: booking_created, booking_updated, booking_cancelled, reminder_24h, reminder_2h
- Template variables (client name, time, address, cancellation link)
- Scheduler/queue for delayed reminders
- Opt-in/opt-out preferences
- Delivery provider abstraction (SMTP + SMS gateway placeholder)

## Success Criteria
- Booking confirmation is sent reliably
- Reminders send at configured offsets
- Failed deliveries are logged + retry policy exists

## Related Tasks
- T-060: Notification service
- T-061: Template rendering
- T-062: Scheduler/queue integration

## Related Documentation
- docs/architecture/notifications.md (Planned)
