# Graphify Setup and Usage Guide
## Windows 11 · Codebuff · Spring Boot HRMS

This guide explains how to use Graphify to inspect the relationships between code in the `my_hourly` Spring Boot backend, refresh the graph after code changes, and use it from Codebuff.

## 1. Project location

Open PowerShell in the project root:

```powershell
cd "C:\Users\User\Desktop\Office Projects\my_hourly"
```

Your graph file is expected at:

```text
graphify-out\graph.json
```

Check that it exists:

```powershell
Test-Path .\graphify-out\graph.json
```

Expected result: `True`.

## 2. Graphify version and executable

The `uv` and `uvx` commands may not be available by their short names in every terminal. Define the full paths for the current PowerShell session:

```powershell
$uv = "$env:LOCALAPPDATA\Microsoft\WinGet\Packages\astral-sh.uv_Microsoft.Winget.Source_8wekyb3d8bbwe\uv.exe"
$uvx = "$env:LOCALAPPDATA\Microsoft\WinGet\Packages\astral-sh.uv_Microsoft.Winget.Source_8wekyb3d8bbwe\uvx.exe"
```

Verify the `uv` executable:

```powershell
Test-Path $uv
& $uv tool list
```

The installed `graphifyy` tool should show version `0.9.82` in the environment verified during setup.

Run Graphify through `uvx` at a pinned version to avoid accidentally invoking the older executable:

```powershell
& $uvx --from "graphifyy==0.9.82" graphify --version
```

Expected output:

```text
graphify 0.9.82
```

In the current setup, `Get-Command graphify -All` resolved an older executable under `AppData\Local\Python\pythoncore-3.14-64\Scripts`. Prefer the pinned `uvx` command in this guide unless that old installation is intentionally repaired.

**Note:** `$uvx` is a PowerShell variable for the current terminal session. Define it again when you open a new terminal, or use the full path directly.

## 3. Refresh the graph after code changes

For the current setup, which does not use an LLM API key, run this from the project root after changing code:

```powershell
& $uvx --from "graphifyy==0.9.82" graphify . --code-only
```

This indexes supported source code locally and avoids semantic extraction of documentation, papers, and images. It does not provide the same non-code semantic coverage as a full extraction.

Verify the graph file exists afterward:

```powershell
Test-Path .\graphify-out\graph.json
```

If the command reports an error, read the complete output; `True` only confirms that a graph file exists, not that the latest refresh succeeded.

### Alternative: full semantic extraction

A full update may try to process documentation, papers, and images and can require a supported LLM API key. Without one, the command may stop with a “no LLM API key found” error.

Do not assume the full update succeeded if it reports that error. Use `--code-only` when you need a code-focused graph without an API key. If you need semantic indexing of non-code files, configure a supported backend and its API key securely, following Graphify's current documentation. Do not put API keys in source control.

## 4. Commands at a glance

| Task | Command |
|---|---|
| Set `uvx` for this terminal | `$uvx = "$env:LOCALAPPDATA\Microsoft\WinGet\Packages\astral-sh.uv_Microsoft.Winget.Source_8wekyb3d8bbwe\uvx.exe"` |
| Check Graphify version | `& $uvx --from "graphifyy==0.9.82" graphify --version` |
| Refresh code graph | `& $uvx --from "graphifyy==0.9.82" graphify . --code-only` |
| Confirm graph file exists | `Test-Path .\graphify-out\graph.json` |
| Check installed uv tools | `& $uv tool list` |
| Check which executable PowerShell resolves | `Get-Command graphify -All \| Select-Object Source` |

## 5. Git hooks and when they run

Graphify hooks were installed in this repository for `post-commit` and `post-checkout`, and a merge driver was registered.

These hooks run for their configured Git operations; they do **not** update the graph on every file save. A normal edit in IntelliJ or Codebuff may therefore leave the graph stale until you refresh it.

For predictable results after editing code, run the code-only refresh command in Section 3. After a pull, merge, or branch switch, refresh manually if you need to be certain the graph represents the current working tree.

If a hook fails or behaves unexpectedly, inspect the Git command output and check hook status using the Graphify CLI version that is actually being invoked. Do not reinstall hooks blindly.

## 6. Codebuff MCP configuration

The project has an MCP configuration at:

```text
.agents\mcp.json
```

The Graphify MCP server should point to the existing graph file. Because the short `uvx` command was not recognized in Codebuff, use the full executable path in the `command` field. Example configuration:

```json
{
  "mcpServers": {
    "graphify": {
      "command": "C:\\Users\\User\\AppData\\Local\\Microsoft\\WinGet\\Packages\\astral-sh.uv_Microsoft.Winget.Source_8wekyb3d8bbwe\\uvx.exe",
      "args": [
        "--from",
        "graphifyy==0.9.82",
        "graphify-mcp",
        "--graph",
        "C:\\Users\\User\\Desktop\\Office Projects\\my_hourly\\graphify-out\\graph.json"
      ]
    }
  }
}
```

JSON requires backslashes to be escaped as `\\`. If your executable or project location differs, change the paths accordingly. Preserve any other existing MCP servers in the file.

After changing MCP configuration or refreshing the graph, reconnect/restart the Graphify MCP server in Codebuff if needed, and test a graph query. Available tools in the verified setup included `query_graph`, `get_node`, `get_neighbors`, `get_community`, `god_nodes`, `graph_stats`, and `shortest_path`.

## 7. Recommended workflow for AI-assisted code changes

1. Ask the coding agent to query Graphify before changing a feature.
2. Ask it to trace the relevant controller, service, validator, repository, entity, DTO, security rules, database migrations, and tests as applicable.
3. Treat actual source files as the source of truth; Graphify is a navigation aid and may be stale.
4. Ask for the smallest change that preserves existing business rules.
5. Review the diff and run relevant tests/build checks.
6. Refresh the graph using the code-only command.
7. Query Graphify again when you need to inspect the updated relationships.

The project's `AGENTS.md` contains the development conventions already established for this workflow. Keep those rules there rather than adding them as fake MCP server entries.

## 8. Troubleshooting

### `uv` or `uvx` is not recognized
Use the full paths and define `$uv` / `$uvx` as shown in Section 2.

### Graphify shows version 0.9.27
PowerShell is likely invoking the older standalone executable. Use:

```powershell
& $uvx --from "graphifyy==0.9.82" graphify --version
```

### `no LLM API key found`
Use `graphify . --code-only` through the pinned `uvx` command, or configure a supported LLM backend if you need full semantic indexing.

### `graph.json` exists but the graph may be stale
The existence check does not confirm freshness. Run the refresh command and inspect its output for errors.

### Codebuff cannot start the MCP server
Confirm the full `uvx.exe` path in `.agents\mcp.json`, validate JSON quoting/escaping, confirm the graph path exists, then reconnect the server.

## 9. Safety notes

- Do not commit API keys or secrets.
- Do not stage or commit all project files indiscriminately; review `git status` and `git diff` first.
- Avoid deleting `graphify-out` or reinstalling hooks unless you understand the effect and have a recovery plan.
- Graphify results are a map of code relationships, not a substitute for reading and testing the source.
