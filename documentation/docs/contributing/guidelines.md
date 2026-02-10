---
sidebar_position: 1
title: Contributing Guidelines
---

# Contributing Guidelines

Thank you for contributing to TAFLEX! Use these guidelines to keep the framework consistent and stable.

## Before You Start

- Check existing [GitHub Issues](https://github.com/vinipx/taflex/issues) or open a new one to discuss changes
- Keep changes focused and small where possible
- Follow the project conventions for tests, naming, and configuration

## Development Workflow

### 1. Fork and Clone

```bash
git clone https://github.com/your-username/taflex.git
cd taflex
./setup.sh
```

### 2. Create a Feature Branch

Use conventional branch naming:

```bash
git checkout -b feature/add-desktop-driver
git checkout -b fix/locator-cache-issue
git checkout -b docs/update-api-reference
```

### 3. Implement Changes

- Add or update tests covering your change
- Update documentation if you add or modify a public API
- Ensure all existing tests still pass

### 4. Run Tests

```bash
# Quick validation
./gradlew smokeTest

# Full suite for your area
./gradlew webTest    # if you changed web driver
./gradlew apiTest    # if you changed API driver
./gradlew mobileTest # if you changed mobile driver

# Full build
./gradlew build -x test
./gradlew compileTestJava
```

### 5. Submit a Pull Request

- Use a clear title following conventional commit format: `feat(driver): add desktop driver strategy`
- Include a summary of **what** and **why**
- Describe how to test the change
- Add screenshots or logs if relevant

## Code Standards

### General

- Tests extend `BaseTest` and use `DriverFactory` for driver creation
- Use TestNG groups: `smoke`, `regression`, `web`, `api`, `mobile`
- Avoid hardcoded URLs and locators in test classes
- Prefer SLF4J `logger` instead of `System.out`

### Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Test class | `{Feature}Tests` | `LoginTests`, `UserApiTests` |
| Test method | `should{ExpectedBehavior}` | `shouldLoginSuccessfully` |
| Locator key | `{page}.{element}.{type}` | `login.username.field` |
| Properties file | `{feature}.properties` | `checkout.properties` |

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat(driver): add desktop driver strategy
fix(locator): resolve cache invalidation on reload
docs(api): update DatabaseManager javadoc
test(web): add checkout flow regression tests
chore(ci): update GitHub Actions workflow
```

## Documentation

- Keep docs under `documentation/docs/` in Markdown
- Update `documentation/sidebars.js` when adding new pages
- Keep examples minimal and focused on intent
- Use Mermaid diagrams for architecture visuals

### Building Docs Locally

```bash
cd documentation
npm install
npm run start
```

## Reporting

- Align ReportPortal metadata and suite names with TestNG groups
- All TestNG suites go in `src/test/resources/testng/`

## Pull Request Checklist

- [ ] Code compiles without warnings
- [ ] All relevant tests pass
- [ ] Documentation updated (if applicable)
- [ ] Commit messages follow conventional format
- [ ] No hardcoded values in test code
- [ ] Locators externalized in `.properties` files

---

**Questions?** Open a [GitHub Issue](https://github.com/vinipx/taflex/issues) or contact the maintainers.
