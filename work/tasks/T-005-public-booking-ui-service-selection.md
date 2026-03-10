# T-005: Public Booking UI – Service Selection

- Parent: F-001
- Type: Task
- Status: Done
- Feature: F-001
- Owner: frontend
- Related modules: public-ui
- Depends on: T-003

## Goal
Show active services to customers as the first step of the booking flow.

## Scope
- Public endpoint consumption (from admin services list or dedicated public endpoint):
    - display active services only
- Service card/list item includes:
    - name, duration, price (if set), short description
- Selection persists to next step (date/time selection in later features)

## Out of Scope
- Slot picking (F-003)
- Booking creation (F-004)
- Staff selection (F-002)

## Definition of Done
- Customer can view and select a service
- Archived services never appear
- UI state is preserved on refresh (e.g., query param or local state strategy)
