# F-021: MCP Integration

- Type: Feature
- Status: Backlog
- Source: Product strategy (AI-native integrations)

## Description
Implement an MCP server for the reservation system so AI agents and IDE tooling can query availability,
browse catalog data, and later execute booking workflows through a stable machine-oriented interface.

## Background
The reservation domain is already exposed through HTTP APIs and internal application services, but MCP
adds a higher-level integration surface for AI-assisted workflows. This is not a novelty add-on; it is
an opportunity to make the system easier to integrate, automate, and differentiate from commodity booking
products.

Expected product value:
1. Faster AI-assisted booking and support flows
2. Easier integrations for agent tooling and IDE automation
3. A differentiated interface beyond standard admin/public web flows

## Scope
- Add an MCP server module/process for the reservation system
- Expose read-only tools for business, service, staff, and availability lookup
- Expose carefully scoped booking actions after read paths are stable
- Reuse application services and validation rules instead of duplicating domain logic
- Define machine-friendly tool schemas and error contracts

## Non-Goals
- Bypassing existing business validation or permission rules
- Direct repository-level access from MCP tools
- Broad write access without explicit authorization and audit rules

## Success Criteria
- MCP tools can retrieve catalog and availability data through stable schemas
- MCP booking actions reuse existing application rules and validations
- Tool failures return actionable machine-readable errors
- The MCP layer is covered by focused integration tests and basic operational docs

## Related Tasks
- T-023: MCP architecture and transport design
- T-024: Read-only MCP tools for catalog and availability
- T-025: Booking MCP tools and validation flow
- T-026: MCP auth, permissions, and audit guardrails

## Related Documentation
- docs/architecture/mcp-integration.md (Planned)
