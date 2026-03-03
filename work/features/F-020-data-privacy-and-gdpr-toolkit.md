# F-020: Data Privacy & GDPR Toolkit (Souhlasy, export, mazání)

- Type: Feature
- Status: Todo
- Source: EU compliance baseline

## Description
Add GDPR-oriented features: consent tracking, data export, and data deletion/anonymization workflows.

## Background
You will process personal data (name, phone, health notes potentially). Compliance matters, especially in EU.

Current implementation gaps:
1. No consent model
2. No export/delete workflows

## Scope
- Consent flags: marketing, reminders, terms acceptance (timestamp + source)
- Data export per client (JSON/CSV)
- Delete/anonymize client data with constraints (keep accounting records if needed)
- Admin UI flows + audit events

## Success Criteria
- Consents are stored with timestamps
- Export is complete and readable
- Deletion/anonymization is safe and logged

## Related Tasks
- T-190: Consent schema
- T-191: Export pipeline
- T-192: Deletion/anonymization pipeline

## Related Documentation
- docs/security/gdpr.md (Planned)