# F-010: Embeddable Booking Widget (Vložení na vlastní web)

- Type: Feature
- Status: Backlog
- Source: Product baseline (integrations)

## Description
Provide an embeddable widget (iframe or JS snippet) that can be placed on a customer's website and open booking flow.

## Background
Many will already have WordPress/Webflow and want booking there.

Current implementation gaps:
1. No embed mode
2. No cross-domain configuration

## Scope
- Embed script/snippet generator in admin
- Widget modes: inline + button modal
- Allowed domains list (basic security)
- Pass theme parameters (color, font optional)

## Success Criteria
- Widget works on external domains
- Booking created correctly and attributed to business
- Basic protection against unauthorized embedding

## Related Tasks
- T-090: Embed mode UI
- T-091: Domain allowlist
- T-092: Widget packaging

## Related Documentation
- docs/integrations/widget.md (Planned)
