# F-012: Blocks / Time Off (Blokace času)

- Type: Feature
- Status: Backlog
- Source: Product baseline (operations)

## Description
Allow admins to block time ranges (vacation, lunch, maintenance) that behave like bookings for availability.

## Background
Not all unavailability is a "booking". Blocks need their own type and reason.

Current implementation gaps:
1. No block entity
2. Availability ignores manual blocks

## Scope
- Block entity: staffId optional, start/end, reason, visibility (internal)
- Create/edit blocks in admin calendar
- Blocks integrate into conflict checking and slot generation

## Success Criteria
- Blocks prevent bookings in that time range
- Blocks can be recurring (optional phase 2)
- Blocks are visible in admin calendar

## Related Tasks
- T-110: Block model + CRUD
- T-111: Calendar UI support
- T-112: Availability integration

## Related Documentation
- docs/domain/blocks.md (Planned)
