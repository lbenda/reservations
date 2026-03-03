# F-006: Client Database (CRM)

- Type: Feature
- Status: Todo
- Source: Product baseline (client management)

## Description
Maintain a client database with history of bookings, notes, and basic segmentation (e.g., VIP, frequent).

## Background
For massage practice, client notes (contraindications, preferences) and retention are key.

Current implementation gaps:
1. Bookings not linked to client identity
2. No searchable client list

## Scope
- Client entity: name, email, phone, tags, notes
- Auto-create client from booking (dedupe by email/phone)
- Client profile page: booking history, cancellations, no-shows
- Search & filter (name/email/phone/tag)

## Success Criteria
- New booking creates/links client record correctly
- Admin can search clients quickly
- Client history is accurate and complete

## Related Tasks
- T-050: Data model - Client
- T-051: Dedupe strategy
- T-052: Admin UI - CRM list + profile

## Related Documentation
- docs/domain/client.md (Planned)
