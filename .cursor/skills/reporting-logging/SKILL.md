---
name: reporting-logging
description: Defines logging and reporting practices using SLF4J, Logback, and ReportPortal. Use when adding logs, test metadata, or report integration.
---
# Reporting and Logging

## Quick start
- Use SLF4J `logger` for all test logs.
- Keep logs concise and actionable (include test names and IDs).
- Align TestNG groups with ReportPortal suite naming.

## Checklist
- [ ] No `System.out` in tests
- [ ] Failure logs include root cause
- [ ] Screenshots captured on failure when enabled

## Example
```java
logger.info("Starting test: {}", result.getName());
logger.error("Failure reason: {}", result.getThrowable().getMessage());
```
