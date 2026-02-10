---
sidebar_position: 1
title: Test Design Best Practices
---

# Test Design Best Practices

These guidelines help keep tests reliable, readable, and fast across Web, API, and Mobile suites in TAFLEX.

## Core Principles

### 1. Keep Tests Orchestration-Only

Test methods should read like a script of user actions and assertions. All low-level implementation details belong in driver strategies and element wrappers.

```java
// ✅ Good — reads as a clear scenario
@Test(groups = {"smoke"}, description = "Verify successful login")
public void shouldLoginSuccessfully() {
    driver.navigateTo("login.page.url");
    driver.type("login.username.field", "testuser");
    driver.type("login.password.field", "password123");
    driver.click("login.submit.button");
    Assert.assertTrue(driver.isVisible("dashboard.welcome.message"));
}

// ❌ Bad — leaking implementation details
@Test
public void testLogin() {
    Page page = ((PlaywrightDriverStrategy) driver).getPage();
    page.navigate("https://staging.example.com/login");
    page.locator("#username").fill("testuser");
    // ...
}
```

### 2. Prefer Deterministic Checks

Avoid `Thread.sleep()` and unstable timing assumptions. Use framework-level waits.

```java
// ✅ Good — explicit wait
driver.waitForVisible("dashboard.welcome.message", 10);

// ❌ Bad — arbitrary sleep
Thread.sleep(5000);
Assert.assertTrue(driver.isVisible("dashboard.welcome.message"));
```

### 3. Use Explicit Assertions

Make each assertion focused and include a descriptive message.

```java
// ✅ Good — clear intent
Assert.assertTrue(driver.isVisible("dashboard.welcome.message"),
    "Welcome message should be visible after successful login");

// ❌ Bad — no message, unclear intent
Assert.assertTrue(driver.isVisible("dashboard.welcome.message"));
```

### 4. Externalize Everything

Locators and test data belong in `src/test/resources`, never hardcoded in tests.

```java
// ✅ Good — externalized locator
driver.type("login.username.field", "testuser");

// ❌ Bad — hardcoded selector
((PlaywrightDriverStrategy) driver).getPage().locator("#username").fill("testuser");
```

## Structure and Naming

### Test Class Names

- End with `Tests` and reflect the feature under test
- Example: `LoginTests`, `UserApiTests`, `MobileNavigationTests`

### Test Method Names

- Start with `should` and describe expected behavior
- Example: `shouldLoginSuccessfully`, `shouldReturnHealthyStatus`

### Test Groups

Use TestNG groups for flexible execution:

| Group | Purpose | Command |
|-------|---------|---------|
| `smoke` | Quick health checks | `./gradlew smokeTest` |
| `regression` | Full coverage | `./gradlew regressionTest` |
| `web` | Web-specific tests | `./gradlew webTest` |
| `api` | API-specific tests | `./gradlew apiTest` |
| `mobile` | Mobile-specific tests | `./gradlew mobileTest` |

### Test Descriptions

Always include a `description` in `@Test` that explains the business intent:

```java
@Test(
    groups = {"smoke", "regression"},
    description = "Verify successful login with valid credentials",
    priority = 1
)
public void shouldLoginSuccessfully() { ... }
```

## Data and Preconditions

### Create Preconditions Inside the Test

Tests should be self-contained. If a test needs to delete something, create it first.

```java
@Test(description = "Verify user deletion")
public void shouldDeleteUser() {
    // Arrange — create the user to delete
    ApiDriverStrategy api = (ApiDriverStrategy) driver;
    api.post("user.create.endpoint", createUserPayload);

    // Act — delete the user
    ApiDriverStrategy.ApiResponse response = api.delete("user.delete.endpoint");

    // Assert
    Assert.assertEquals(response.getStatusCode(), 200);
}
```

### Avoid Shared State

Tests should not depend on execution order. Each test must set up and clean up its own state.

```java
// ✅ Good — self-contained
@BeforeMethod
public void setUpTestData() {
    DatabaseManager db = DatabaseManager.getInstance();
    db.executeUpdate("INSERT INTO users ...", "testuser");
}

@AfterMethod
public void tearDownTestData() {
    DatabaseManager db = DatabaseManager.getInstance();
    db.executeUpdate("DELETE FROM users WHERE username = ?", "testuser");
}
```

### Use Data Providers for Multiple Scenarios

```java
@DataProvider(name = "loginCredentials")
public Object[][] loginCredentials() {
    return new Object[][] {
        {"validuser", "validpass", true},
        {"invaliduser", "wrongpass", false},
        {"lockeduser", "validpass", false}
    };
}

@Test(dataProvider = "loginCredentials")
public void shouldHandleLoginScenarios(String user, String pass, boolean shouldSucceed) {
    driver.navigateTo("login.page.url");
    driver.type("login.username.field", user);
    driver.type("login.password.field", pass);
    driver.click("login.submit.button");

    if (shouldSucceed) {
        Assert.assertTrue(driver.isVisible("dashboard.welcome.message"));
    } else {
        Assert.assertTrue(driver.isVisible("login.error.message"));
    }
}
```

## Reliability Tips

### Avoid Sleeps

Use strategy-level waits or framework waits instead of `Thread.sleep()`.

### Isolate Side Effects

Clean up any created data in `@AfterMethod` or within the test itself.

### Limit Scope

One behavior per test. Don't combine login verification and dashboard navigation in a single test.

### Use Retry for Known Flaky Scenarios

TAFLEX includes a built-in `RetryAnalyzer` that automatically retries failed tests:

```properties
retry.enabled=true
retry.max.attempts=2
```

### Use Soft Assertions for Multiple Checks

When validating multiple independent conditions on a single page:

```java
@Test
public void shouldDisplayDashboardCorrectly() {
    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(driver.isVisible("dashboard.header")).isTrue();
    softly.assertThat(driver.isVisible("dashboard.welcome.message")).isTrue();
    softly.assertThat(driver.isVisible("dashboard.user.menu")).isTrue();
    softly.assertAll(); // Reports all failures at once
}
```

## Locator Best Practices

### Naming Convention

Use `{page}.{element}.{type}` format:

```properties
# ✅ Good
login.username.field=#username
login.submit.button=button[type='submit']
dashboard.welcome.message=.welcome-message

# ❌ Bad
usr=#username
btn=button[type='submit']
```

### Selector Priority

1. **ID selectors**: `#username` (most stable)
2. **Data attributes**: `[data-testid='login-button']`
3. **CSS selectors**: `button[type='submit']`
4. **XPath**: `//android.widget.Button[@text='Login']` (mobile only when needed)

### Avoid Fragile Selectors

```properties
# ✅ Good — stable selectors
login.button=[data-testid='login-button']
login.button=#submit

# ❌ Bad — fragile selectors
login.button=/html/body/div[3]/div[2]/button
login.button=.sc-12dfef-3
```

## Platform-Specific Guidelines

### Web Tests (Playwright)

- Playwright has built-in auto-wait, so explicit waits are rarely needed
- Use CSS selectors over XPath for better performance
- Enable tracing for debugging: `web.tracing.enabled=true`

### API Tests (HttpClient)

- Always validate both status code and response body
- Use externalized endpoint paths from `api/endpoints.properties`
- Set appropriate headers via `setDefaultHeader()`

### Mobile Tests (Appium)

- Use accessibility IDs when available (`@content-desc` for Android, `accessibilityIdentifier` for iOS)
- Account for keyboard visibility — use `hideKeyboard()` when needed
- Keep mobile-specific locators in `mobile/selectors.properties`

## Example: Complete Test Pattern

```java
@Test(groups = {"smoke", "api"}, description = "Verify GET orders endpoint returns 200")
public void shouldGetOrdersSuccessfully() {
    // Arrange
    ApiDriverStrategy api = (ApiDriverStrategy) driver;

    // Act
    ApiDriverStrategy.ApiResponse response = api.get("orders.endpoint");

    // Assert
    Assert.assertEquals(response.getStatusCode(), 200, "Orders endpoint should return 200");
    Assert.assertNotNull(response.getBody(), "Response body should not be null");
    Assert.assertTrue(response.isSuccessful(), "Response should be successful");
}
```

---

**Remember**: Good tests are readable, independent, deterministic, and fast. Follow these practices and your test suite will be a reliable safety net for your application.
