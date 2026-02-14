# Cloud Testing Guide

TAFLEX provides native integration with industry-leading cloud testing platforms like **BrowserStack** and **SauceLabs**. This allows you to scale your test execution across a vast array of browser/OS combinations and real mobile devices.

## 🚀 Why Use Cloud Testing?

- **Infrastructure Agnostic**: No need to maintain local browser versions or mobile emulators.
- **Parallel Execution**: Run tests simultaneously across multiple configurations.
- **Real Devices**: Test your mobile applications on actual physical hardware in the cloud.
- **Rich Debugging**: Access video recordings, network logs, and screenshots of every test session.

## 🛠️ Configuration

To enable cloud execution, update your `automation.properties` file with the following parameters:

### Common Settings

```properties
# Set the platform (local, browserstack, saucelabs)
cloud.platform=browserstack

# Your cloud credentials
cloud.user=your_username
cloud.key=your_access_key
```

### Web Execution

For web automation, you can specify the target environment:

```properties
execution.mode=web
browser=chromium
os=Windows
os.version=11
```

### Mobile Execution

For mobile automation, the framework automatically maps these settings to Appium capabilities via `CapabilityBuilder`:

```properties
execution.mode=mobile
os=Android
os.version=13.0
mobile.device.name=Google Pixel 7
```

---

## 🌩️ Supported Providers

### BrowserStack

When `cloud.platform=browserstack` is set, the framework uses the `CapabilityBuilder` to generate `bstack:options`. It supports both Playwright (remote connection) and Appium.

### SauceLabs

When `cloud.platform=saucelabs` is set, the framework generates `sauce:options` and configures the remote URL to point to the SauceLabs grid.

---

## 💻 Execution

Simply run your standard Gradle test tasks. The framework detects the cloud configuration and establishes the remote connection automatically:

```bash
# Run web tests on the configured cloud platform
./gradlew webTest

# Run mobile tests on the configured cloud platform
./gradlew mobileTest
```

## 🔍 Debugging in the Cloud

Once a test finishes, you can log in to your provider's dashboard to see:
1. **Video Recording**: A full video of the test execution.
2. **Metadata**: Detailed information about the browser, OS, and environment.
3. **Logs**: Appium logs, console logs, and network traffic.
