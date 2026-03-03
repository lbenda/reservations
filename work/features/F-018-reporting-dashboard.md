# F-018: Reporting Dashboard (Statistiky)

- Type: Feature
- Status: Todo
- Source: Product baseline (analytics)

## Description
Provide business metrics: bookings over time, revenue, no-show rate, utilization per staff, top services.

## Background
Operators want insight; it also becomes a paid-plan differentiator.

Current implementation gaps:
1. No aggregates pipeline
2. No dashboard UI

## Scope
- Metrics queries (daily/weekly/monthly)
- Filters: location, staff, service
- Export CSV
- Basic charts (implementation detail)

## Success Criteria
- Dashboard loads under reasonable time for 12 months
- Metrics match underlying bookings/payments
- Exports work and are correctly scoped by permissions

## Related Tasks
- T-170: Analytics queries
- T-171: Dashboard UI
- T-172: CSV export

## Related Documentation
- docs/analytics/metrics.md (Planned)
