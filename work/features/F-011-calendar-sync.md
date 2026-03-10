# F-011: Calendar Sync (Google/Outlook/iCal)

- Type: Feature
- Status: Backlog
- Source: Product baseline (calendar integration)

## Description
Sync bookings to external calendars and optionally import busy blocks from external calendars (1-way or 2-way).

## Background
Therapists often manage personal and work calendars together.

Current implementation gaps:
1. No external calendar adapters
2. No conflict blocks from external sources

## Scope
- Export feed (ICS) per staff or business
- OAuth integration placeholders for Google/Microsoft (phased)
- Import busy events -> treat as blocks in availability engine
- Sync frequency + manual refresh

## Success Criteria
- ICS export reflects bookings accurately
- Imported busy time blocks availability
- No duplicate events on repeated sync

## Related Tasks
- T-100: ICS export
- T-101: Busy blocks import model
- T-102: Availability engine integration

## Related Documentation
- docs/integrations/calendar-sync.md (Planned)
