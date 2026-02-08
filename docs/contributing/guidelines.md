# Contributing Guidelines

Thank you for contributing to TAFLEX! Use these guidelines to keep the framework consistent and stable.

## Before you start
- Check existing issues or open a new one to discuss changes.
- Keep changes focused and small where possible.
- Follow the project conventions for tests, naming, and configuration.

## Development workflow
1. Create a feature branch from `main`.
2. Implement the change with tests or documentation updates.
3. Run the relevant suite (e.g., `./gradlew apiTest`, `./gradlew webTest`).
4. Submit a pull request with a clear description and test results.

## Code standards
- Tests extend `BaseTest` and use `DriverFactory`.
- Use TestNG groups: `smoke`, `regression`, `web`, `api`, `mobile`.
- Avoid hardcoded URLs and locators in tests.
- Prefer SLF4J `logger` instead of `System.out`.

## Documentation
- Keep docs under `docs/` in Markdown.
- Update navigation (`mkdocs.yml`) when adding new pages.
- Keep examples minimal and focused on the intent.

## Reporting
- Align ReportPortal metadata and suite names with TestNG groups.

## Pull requests
Include:
- What and why (summary)
- How to test
- Any screenshots or logs if relevant
