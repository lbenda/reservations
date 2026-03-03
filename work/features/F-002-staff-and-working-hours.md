# F-002: Staff & Working Hours

- Type: Feature
- Status: Todo
- Source: Product baseline (Reservio-like core)

## Description
Support one or more staff members with per-staff working hours, breaks and service assignment.

## Background
In massage practice you may start solo, but growth requires staff support and per-therapist availability.

Current implementation gaps:
1. No staff entity
2. No working hours rules per staff

## Scope
- Staff entity: name, roleLabel, active, contact (optional)
- Weekly schedule (Mon–Sun): time ranges
- Exceptions: days off, special opening hours
- Assign services to staff (who can deliver what)
- Booking flow: choose staff (optional/required setting)

## Success Criteria
- Availability differs by staff schedules
- Booking can be filtered by selected staff
- Exceptions override weekly schedule

## Related Tasks
- T-010: Data model - Staff + schedules
- T-011: Admin UI - Staff management
- T-012: Availability engine - staff schedule input

## Related Documentation
- docs/domain/staff.md (Planned)
