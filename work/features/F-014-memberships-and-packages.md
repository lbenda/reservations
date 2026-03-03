# F-014: Memberships & Packages (Permanentky / balíčky)

- Type: Feature
- Status: Todo
- Source: Product baseline (retention)

## Description
Support packages (e.g., 5 massages prepaid) and memberships with usage tracking and redemption during booking.

## Background
Massage practices often sell packages; it’s a strong differentiator vs simple booking tools.

Current implementation gaps:
1. No entitlements ledger
2. No redemption at booking time

## Scope
- Package product: units, validFrom/To, applicable services
- Client entitlement ledger: add/redeem/expire adjustments
- Booking option: pay normally or redeem entitlement
- Admin: sell/grant package, view balances

## Success Criteria
- Redemption decreases balance exactly once
- Expired packages cannot be redeemed
- Reports show package utilization

## Related Tasks
- T-130: Entitlement ledger
- T-131: Booking redemption flow
- T-132: Admin package management

## Related Documentation
- docs/domain/packages.md (Planned)
