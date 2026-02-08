---
name: web-automation
description: Guides Playwright-based web automation using framework driver strategies. Use when creating or updating web tests.
---
# Web Automation

## Quick start
- Use `PlaywrightDriverStrategy` via `DriverFactory`.
- Use locator keys resolved by `LocatorStrategy`.
- Prefer strategy-level waits over manual sleeps.

## Setup notes
- Install browsers with Gradle task `installPlaywright` when needed.
- Use `ConfigManager` for base URL and environment settings.

## Checklist
- [ ] No direct Playwright instantiation in tests
- [ ] Selectors externalized under `src/test/resources`
- [ ] Assertions validate visible behavior, not implementation details

## Example
```java
driver.navigateTo("login.page.url");
driver.type("login.username.field", username);
driver.type("login.password.field", password);
driver.click("login.submit.button");
```
