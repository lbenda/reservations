# T-024: Read-only MCP Tools

- Type: Task
- Status: Backlog
- Feature: F-021
- Owner: backend

## Goal
Expose safe read-only MCP tools for catalog and availability workflows.

## Scope
- Add tools to list businesses, services, and eligible staff
- Add tools to query availability for a service with optional staff filtering
- Return stable machine-friendly payloads suitable for agent consumption
- Reuse existing application services and validation logic

## Definition of Done
- MCP clients can retrieve catalog and availability data without direct HTTP-specific coupling
- Tool outputs are deterministic and documented by tests
- Invalid input returns consistent structured errors
