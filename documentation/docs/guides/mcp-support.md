# Model Context Protocol (MCP) Support

TAFLEX provides native support for the [Model Context Protocol (MCP)](https://modelcontextprotocol.io/), allowing you to connect your test automation framework directly to AI assistants like Claude, Cursor, and the Gemini CLI.

By enabling MCP, the AI can "understand" your project's configuration, inspect available locators, and even execute test suites on your behalf.

## Available Tools

The TAFLEX MCP server exposes the following tools:

| Tool | Description | Parameters |
|------|-------------|------------|
| `get_config` | Retrieve current TAFLEX configuration properties. | None |
| `get_execution_mode` | Get current execution mode (`web`, `api`, `mobile`). | None |
| `list_locators` | List all available logical locators. | `pattern` (optional) |
| `get_locator` | Get details of a specific locator. | `name` (required) |
| `run_suite` | Execute a specific TestNG suite via Gradle. | `suite` (required) |

---

## Configuration

To use the TAFLEX MCP server, you need to point your MCP client to the Gradle `runMcp` task.

### 1. Claude Desktop

Add the following to your `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "taflex-java": {
      "command": "./gradlew",
      "args": ["runMcp", "-q"],
      "cwd": "/path/to/your/taflex"
    }
  }
}
```

### 2. Cursor

1. Open Cursor Settings.
2. Navigate to **Features** > **MCP**.
3. Click **+ Add New MCP Server**.
4. Set the name to `taflex-java`.
5. Set the type to `command`.
6. Set the command to:
   ```bash
   ./gradlew runMcp -q
   ```

### 3. Gemini CLI

If you are using the Gemini CLI, you can activate the server by adding it to your configuration or running with the server flag:

```bash
gemini --mcp taflex-java:./gradlew runMcp -q
```

---

## Use Cases

### Inspecting Locators
**User:** "Show me all the locators for the login page."
**AI:** *Calls `list_locators(pattern="login")` and displays the results.*

### Validating Locators
**User:** "What is the selector for `login.button`?"
**AI:** *Calls `get_locator(name="login.button")` and returns the CSS/XPath.*

### Running Tests
**User:** "Run the smoke tests for me."
**AI:** *Calls `run_suite(suite="smoke-suite")`, monitors the Gradle output, and reports the final status (SUCCESS/FAIL).*

### Debugging Configuration
**User:** "What environment is TAFLEX currently targeting?"
**AI:** *Calls `get_config()` and identifies the `environment` and `web.base.url` properties.*

---

## Technical Details

The MCP server is implemented in Java and communicates over `stdio`. It uses the project's own `ConfigManager` and `LocatorFactory` to ensure that the information provided to the AI is always in sync with your actual test execution environment.

To start the server manually for debugging:
```bash
./gradlew runMcp -q
```
