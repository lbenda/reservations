# F-004: Booking Creation Flow

- Type: Feature
- Status: Backlog
- Source: Product baseline (online booking)

## Description
Allow customers to create a booking by selecting service, staff (optional), date/time, and providing contact details.

## Background
This is the end-to-end booking experience for clients.

Current implementation gaps:
1. No booking entity lifecycle
2. No public booking endpoint with validation

## Scope
- Booking entity: id, serviceId, staffId, start, end, status
- Customer fields: name, email, phone (configurable requiredness)
- Validation: slot must be available at time of submit
- Anti-double-booking: transactional lock / unique constraint
- Confirmation page + booking reference

## Success Criteria
- Booking is created only if slot still available
- Conflicts are prevented under concurrency
- Customer receives confirmation message (hook to notifications)

## Related Tasks
- T-030: Data model - Booking
- T-031: Public booking API
- T-032: Concurrency protection

## Related Documentation
- docs/domain/booking.md (Planned)
