# F-008: Online Payments (Zálohy / platby)

- Type: Feature
- Status: Backlog
- Source: Product baseline (monetization)

## Description
Support online payments (full or deposit) during booking, with payment status tracking and refund/cancel rules.

## Background
Deposits can drastically reduce no-shows and stabilize revenue.

Current implementation gaps:
1. No payment entity
2. No provider integration boundary

## Scope
- Payment entity: bookingId, provider, amount, currency, status
- Payment intent creation at booking time (optional config)
- Webhook handler to confirm payment
- Cancellation policy: auto-refund / manual
- Admin view: payment status + reconciliation export

## Success Criteria
- Paid booking shows "paid" state reliably
- Webhooks are verified and idempotent
- Booking cannot be confirmed as paid without provider confirmation

## Related Tasks
- T-070: Payment model + state machine
- T-071: Provider adapter interface
- T-072: Webhooks + idempotency

## Related Documentation
- docs/architecture/payments.md (Planned)
 
