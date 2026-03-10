# F-009: Booking Website / Public Page (Veřejná rezervační stránka)

- Type: Feature
- Status: Backlog
- Source: Product baseline (public booking)

## Description
Provide a public booking page per business with customizable branding and basic business info.

## Background
Many small businesses need a simple hosted booking page without building their own website.

Current implementation gaps:
1. No public business profile
2. No theming

## Scope
- Business profile: name, address, phone, map link, description
- Branding: logo, primary color, cover image (optional)
- Public URL: /b/{slug}
- SEO basics: title/description, open graph
- Language support (cs/en baseline)

## Success Criteria
- Customer can book end-to-end from public page
- Branding settings reflect on public pages
- Public pages are shareable and stable URLs

## Related Tasks
- T-080: Business profile model
- T-081: Public page UI
- T-082: Slug + routing

## Related Documentation
- docs/ui/public-page.md (Planned)
