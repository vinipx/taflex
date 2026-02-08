---
name: mobile-automation
description: Guides Appium-based mobile automation using MobileDriverStrategy. Use when implementing mobile tests or updating capabilities.
---
# Mobile Automation

## Quick start
- Use `MobileDriverStrategy` via `DriverFactory`.
- Keep capabilities and device settings in `ConfigManager`.
- Use locator keys for mobile elements.

## Checklist
- [ ] No hardcoded device or app paths in tests
- [ ] Driver lifecycle handled by `BaseTest`
- [ ] Tests remain platform-agnostic where possible

## Example
```java
driver.navigateTo("mobile.login.screen");
driver.type("mobile.username.field", username);
driver.type("mobile.password.field", password);
driver.click("mobile.login.button");
```
