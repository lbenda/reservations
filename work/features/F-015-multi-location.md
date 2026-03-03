# F-015: Multi-Location (Více poboček)

- Type: Feature
- Status: Todo
- Source: Product baseline (scaling)

## Description
Allow a business account to manage multiple locations with separate schedules, staff assignments and services.

## Background
If you aim at competing, multi-location is commonly requested by growing businesses.

Current implementation gaps:
1. Business model is single-location
2. No scoping by location

## Scope
- Location entity: name, address, timezone, contact
- Scope staff, services, bookings to location
- Public booking: choose location first (optional)
- Admin filtering by location

## Success Criteria
- No cross-location booking conflicts
- Each location has its own availability rules
- Public links can target specific location

## Related Tasks
- T-140: Location model + migrations
- T-141: Update availability queries with location
- T-142: Admin UI - location switcher

## Related Documentation
- docs/domain/location.md (Planned)
