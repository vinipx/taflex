# Mobile Testing Tutorial

Learn how to automate native and hybrid mobile applications using TAFLEX JS and WebdriverIO (Appium).

## 1. Environment Setup

Mobile testing requires the `mobile` strategy. Ensure you have Appium installed and running locally or have access to a cloud lab.

### Capabilities Configuration
Mobile tests require specific capabilities (platform name, device name, app path, etc).

```java
// Example mobile config for driver.initialize()
const mobileConfig = {
    capabilities: {
        platformName: 'Android',
        'appium:deviceName': 'Pixel_6',
        'appium:app': './apps/my-app.apk',
        'appium:automationName': 'UiAutomator2'
    }
};
```

## 2. Writing a Mobile Test

Set the `mode: 'mobile'` in your spec to use the WebdriverIO-based strategy.

```java
import { test, expect } from '../fixtures.js';

test.describe('Mobile App Login', () => {
    test.use({ mode: 'mobile' });

    test('should login on Android', ({ driver }) => {
        // Load mobile-specific locators
        driver.loadLocators('login');

        const userField = driver.findElement('username_input');
        const passField = driver.findElement('password_input');
        const loginBtn = driver.findElement('submit_button');

        await userField.fill('mobile_user');
        await passField.fill('secret_pass');
        await loginBtn.click();

        const welcome = driver.findElement('welcome_text');
        expect(await welcome.isVisible()).toBeTruthy();
    });
});
```

## 3. Best Practices

- **Selectors**: Use `accessibility id` (ID) or `Xpath` carefully. In TAFLEX JS, store these in `src/resources/locators/mobile/`.
- **Platform Branching**: If your app logic differs significantly between iOS and Android, create separate locator files (e.g., `login_ios.json`, `login_android.json`) and load the correct one at runtime.
- **Wait Strategies**: Mobile networks and devices can be slow. Use `await element.waitFor()` before critical actions.

## Execution on Real Devices (Cloud)

While local emulators are great for development, TAFLEX JS allows you to run these tests on **real devices** via BrowserStack and SauceLabs.

See the [Cloud Execution Tutorial](./cloud-execution.md) to learn how to configure your credentials and target real devices.
