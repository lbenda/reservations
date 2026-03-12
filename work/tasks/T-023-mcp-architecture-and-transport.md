# T-023: MCP Architecture and Transport

- Type: Task
- Status: Backlog
- Feature: F-021
- Owner: backend

## Goal
Define the MCP server structure so tools can safely reuse reservation application services.

## Scope
- Decide whether the MCP server runs in-process with the app or as a separate process/module
- Define transport/bootstrap, dependency wiring, and configuration model
- Define how MCP tools map to application services instead of repositories
- Document tool naming, input/output schemas, and error contract conventions

## Definition of Done
- A concrete MCP server architecture is documented and accepted
- Dependency boundaries between MCP, application services, and persistence are clear
- Initial tool contract conventions are defined for later implementation
