---
name: test-architecture
description: Defines test architecture, suite taxonomy, and layering rules for this unified automation framework. Use when designing test structure, choosing patterns, or reviewing framework-level changes.
---
# Test Architecture

## Quick start
- Keep tests orchestration-only; use driver strategies for implementation.
- All tests extend `BaseTest` and get `AutomationDriver` from `DriverFactory`.
- Use TestNG groups to align with Gradle tasks (`apiTest`, `webTest`, `mobileTest`, `smokeTest`, `regressionTest`).

## Architecture checklist
- [ ] Uses Factory/Strategy resolution via `DriverFactory`
- [ ] No direct Playwright/Appium/HttpClient construction in tests
- [ ] Locators and test data externalized to `src/test/resources`
- [ ] Environment settings read via `ConfigManager`

## Example
```java
public class OrderApiTests extends BaseTest {
    @Test(groups = {"smoke", "api"}, description = "Verify GET orders endpoint returns 200")
    public void shouldGetOrdersSuccessfully() {
        ApiDriverStrategy api = (ApiDriverStrategy) driver;
        ApiResponse response = api.get("orders.endpoint");
        Assert.assertEquals(response.getStatusCode(), 200);
    }
}
```
