# Web Testing Tutorial

Learn how to write robust and maintainable Web tests using TAFLEX and Playwright.

## 1. Organizing Your Tests

TAFLEX uses a clean separation between test logic and locators.

### Step 1: Externalize Locators
Create a properties file for your page in `src/test/resources/locators/web/search.properties`:

```properties
# Search Page Elements
search.input=input[name='q']
search.button=input[type='submit']
```

### Step 2: Create the Test Class
Create your test class in `src/test/java/io/github/vinipx/taflex/tests/web/SearchTests.java` extending `BaseTest`:

```java
package io.github.vinipx.taflex.tests.web;

import io.github.vinipx.taflex.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchTests extends BaseTest {

    @Test(groups = {"smoke", "web"})
    public void shouldSearchSuccessfully() {
        // 1. Navigate (locator defined in global.properties)
        driver.navigateTo("google.home.url");
        
        // 2. Interact using logical names
        driver.type("search.input", "TAFLEX Framework");
        driver.click("search.button");
        
        // 3. Verify visibility
        Assert.assertTrue(driver.isVisible("search.results.container"));
    }
}
```

---

## 2. Best Practices

- **Logical Naming**: Always use logical names like `login.submit.button` instead of hardcoded selectors.
- **BaseTest Lifecycle**: Always extend `BaseTest` to ensure the driver is properly initialized and terminated.
- **Group Your Tests**: Use TestNG groups (`smoke`, `regression`, `web`) to allow flexible execution.

---

## 3. Running Web Tests

Use the dedicated Gradle task to run web tests:

```bash
./gradlew webTest
```

To run in a specific browser or headless mode, update `automation.properties`:

```properties
web.browser=chromium
web.headless=true
```

---

## 4. Automatic Features

- **Screenshots**: TAFLEX automatically captures screenshots on failure in the `screenshots/` directory.
- **Retries**: Flaky tests are automatically retried if enabled in `automation.properties`.
- **Logs**: Detailed browser interactions are logged in `logs/test-automation.log`.
