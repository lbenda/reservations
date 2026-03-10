# F-019: Public API & Webhooks (Integrace pro ajťáky)

- Type: Feature
- Status: Backlog
- Source: Competitive differentiator

## Description
Expose a developer-friendly API and outbound webhooks for booking events to integrate with external systems (CRM, invoicing, etc.).

## Background
As an IT-led project, strong API/webhooks can be your differentiator.

Current implementation gaps:
1. No API keys / OAuth scopes
2. No webhook delivery mechanism

## Scope
- API keys per business (scoped permissions)
- Webhooks: booking.created/updated/cancelled, payment.updated
- Signing (HMAC) and retry policy
- Webhook dashboard (deliveries, failures)

## Success Criteria
- Webhooks are reliable and idempotent
- Signatures verified by consumers
- API access is rate-limited and auditable

## Related Tasks
- T-180: API key management
- T-181: Webhook dispatcher + retries
- T-182: Delivery logs UI

## Related Documentation
- docs/dev/api.md (Planned)
- docs/dev/webhooks.md (Planned)
