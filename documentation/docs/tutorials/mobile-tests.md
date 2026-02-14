# Mobile Testing Tutorial

Learn how to automate native mobile applications using TAFLEX and **Appium**.

---

## 1. Environment Setup

Mobile testing requires the `mobile` strategy. Ensure you have the Appium server installed and reachable.

### Configuration
Update your `automation.properties` with device and app details:

```properties
execution.mode=mobile
mobile.platform=android
mobile.device.name=Pixel_6
mobile.app.path=/path/to/my-app.apk
mobile.appium.url=http://localhost:4723
```

---

## 2. Writing a Mobile Test

Create a test class extending `BaseTest`. The framework handles the Appium driver lifecycle automatically.

```java
package io.github.vinipx.taflex.tests.mobile;

import io.github.vinipx.taflex.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginMobileTests extends BaseTest {

    @Test(groups = {"smoke", "mobile"})
    public void shouldLoginOnMobile() {
        // 1. Use externalized mobile locators
        driver.type("mobile.login.user", "mobile_user");
        driver.type("mobile.login.pass", "secret_pass");
        driver.click("mobile.login.button");

        // 2. Assert visibility using unified API
        Assert.assertTrue(driver.isVisible("mobile.dashboard.welcome"));
    }
}
```

---

## 3. Best Practices

- **Selectors**: Use **Accessibility IDs** whenever possible for better reliability and performance.
- **Wait Strategies**: Mobile interactions can be slower than web. Use `driver.waitForVisible()` for key transitions.
- **Platform Agnostic**: While the locators differ, keep your test logic as platform-agnostic as possible by leveraging the `AutomationDriver` interface.

---

## 4. Running Mobile Tests

Use the dedicated Gradle task:

```bash
./gradlew mobileTest
```

To run on real devices in the cloud (BrowserStack/SauceLabs), simply update your `automation.properties` as described in the [Cloud Execution Tutorial](./cloud-execution.md).
