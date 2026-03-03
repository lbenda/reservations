# F-001: Service Catalog

- Type: Feature
- Status: Todo
- Source: Product baseline

## Description
Implement a service catalog that defines what can be booked (e.g., Massage 60 min, Massage 90 min).
Each service has duration, price, buffers, booking rules and optional resource requirements.

## Background
A booking system needs a canonical definition of bookable items. Availability and pricing derive from service settings.

Current implementation gaps:
1. No persistent model for services
2. No admin UI/API to manage services

## Scope
- Create Service entity: name, description, durationMin, price, currency
- Optional: bufferBeforeMin, bufferAfterMin
- Booking constraints: minAdvanceMin, maxAdvanceDays, cancellationPolicy
- CRUD endpoints and admin screens
- Validation (duration > 0, price >= 0, etc.)

## Success Criteria
- Admin can create/edit/archive services
- Services appear in booking flow
- Service duration affects time slot generation
- Archived services cannot be newly booked

## Related Tasks
- [T-002: Data model - Service](../tasks/T-002-data-model-service.md)
- [T-003: Admin UI - Service CRUD](../tasks/T-003-admin-ui-service-management-screen.md)
- [T-004: Booking UI - Service selection](../tasks/T-004-booking-ui-service-selection.md)
- [T-005: Public Booking UI – Service Selection](../tasks/T-005-public-booking-ui-service-selection.md)
- [T-006: Tests – Service Catalog Coverage](../tasks/T-006-tests-service-catalog-coverage.md)

## Related Documentation
- docs/domain/service.md (Planned)
