### Mandatory workflow for existing backend code

Before modifying existing backend functionality:

1. Query Graphify MCP for the affected components and their dependencies.
2. Read the actual source files and verify the graph findings.
3. Explain the relevant business rules and potential side effects.
4. Implement the smallest necessary change.
5. Run relevant tests and inspect the final diff.
6. Report modified files, test results, and remaining risks.

If Graphify is unavailable or outdated, state that explicitly and inspect the source code before proceeding. Never claim that a check passed unless it was performed.