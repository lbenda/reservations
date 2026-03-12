# T-018: Availability Integration - Staff Schedule Input

- Type: Task
- Status: In review
- Feature: F-002
- Owner: backend

## Goal
Make staff schedule data available as input for the availability engine.

## Scope
- Load weekly schedules and exceptions for a staff member
- Define the service boundary used by availability calculations
- Cover precedence of exceptions over weekly schedule

## Definition of Done
- Availability-related code can query staff schedule data through a clear API
- Exception precedence is explicit and test-covered
