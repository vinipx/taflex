<h1 align="center">TAFLEX</h1>
<p align="center">
  <a href="https://openjdk.org/">
    <img src="https://img.shields.io/badge/Java-21%2B-blue.svg" alt="Java Version"/>
  </a>
  <a href="https://gradle.org/">
    <img src="https://img.shields.io/badge/Gradle-8.5-green.svg" alt="Gradle"/>
  </a>
  <a href="https://playwright.dev/java/">
    <img src="https://img.shields.io/badge/Playwright-1.41.0-2ea44f.svg" alt="Playwright"/>
  </a>
  <a href="https://appium.io/">
    <img src="https://img.shields.io/badge/Appium-9.0.0-6e56cf.svg" alt="Appium"/>
  </a>
  <a href="https://hc.apache.org/httpcomponents-client-4.5.x/">
    <img src="https://img.shields.io/badge/HttpClient-4.5.14-4c7aaf.svg" alt="HttpClient"/>
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/License-Apache%202.0-orange.svg" alt="License"/>
  </a>
  <a href="https://vinipx.github.io/taflex">
    <img src="https://img.shields.io/badge/Docs-TAFLEX-0ea5e9.svg" alt="Documentation"/>
  </a>
</p>

<p align="center">
  <a href="https://vinipx.github.io/taflex">Read the Documentation →</a>
</p>

A production-ready, enterprise-grade unified test automation framework supporting **Web** (Playwright), **API** (Apache HttpClient), and **Mobile** (Appium) testing with externalized locators and configuration.

## 🎯 Key Features

- **Unified Architecture**: Single codebase for Web, API, and Mobile testing.
- **Strategy Pattern**: Runtime driver resolution between platforms.
- **Externalized Locators**: Hierarchical `.properties` locator strategy (Global > Mode > Page).
- **Java 21+ Native**: Leverages modern LTS features for clean and efficient code.
- **Enterprise Reporting**: Native integration with **ReportPortal** and **Xray Cloud** (Jira).
- **Contract Testing**: Integrated support for **Pact** (Consumer-driven).
- **Database Orchestration**: Connection pooling with HikariCP and simplified JDBC wrapper.
- **Quality Gates**: Automatic retries for flaky tests and screenshots on failure.
- **Code Hygiene**: Automated checks with **Checkstyle** and **PMD**.
- **CI/CD Integration**: Out-of-the-box support for **GitHub Actions** and **GitLab CI**.
- **Cloud Ready**: Built-in support for BrowserStack and SauceLabs.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Test Layer                       │
│          (Tests extend BaseTest)                    │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                 DriverFactory                       │
│         (Strategy Pattern - Runtime)                │
└──────────┬─────────────┬──────────────┬─────────────┘
           │             │              │
┌──────────▼──┐ ┌───────▼────┐ ┌──────▼─────┐
│   Web       │ │   API      │ │   Mobile   │
│ (Playwright)│ │ (HttpClient)│ │  (Appium)  │
└─────────────┘ └────────────┘ └────────────┘
           │             │              │
┌──────────▼─────────────▼──────────────▼─────────────┐
│              Externalized Locators                  │
│           (PropertiesLocatorStrategy)               │
└─────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Java 21+** - [Download here](https://adoptium.net/)
- **Gradle 8.5+** - Or use the included wrapper (`./gradlew`)
- **Git** - For version control

### Installation

1. **Clone the repository**:
```bash
git clone https://github.com/vinipx/taflex.git
cd taflex
```

2. **Run setup script**:
```bash
./setup.sh
```

The script will verify your Java version, create `automation.properties` from the template, and compile the test sources.

3. **Configure your environment**:
```bash
# Edit automation.properties with your settings
nano automation.properties
```

### Running Tests

```bash
# Run Web tests
./gradlew webTest

# Run API tests
./gradlew apiTest

# Run Mobile tests
./gradlew mobileTest

# Run via Groups
./gradlew test -Dgroups=smoke
```

## ⚙️ Configuration

Set the mode in `automation.properties`:

```properties
# Execution Mode: web | api | mobile
execution.mode=web

# Web Settings
web.browser=chromium
web.headless=true

# API Settings
api.base.url=https://api.example.com
```

## 📝 Writing Tests

### Web Test Example
```java
public class LoginTests extends BaseTest {
    @Test(groups = {"smoke"})
    public void shouldLoginSuccessfully() {
        driver.navigateTo("login.page.url");
        driver.type("login.username.field", "user");
        driver.type("login.password.field", "pass");
        driver.click("login.submit.button");
        Assert.assertTrue(driver.isVisible("dashboard.welcome"));
    }
}
```

### API Test Example
```java
public class UserApiTests extends BaseTest {
    @Test(groups = {"api"})
    public void shouldFetchUser() {
        ApiDriverStrategy apiDriver = (ApiDriverStrategy) driver;
        ApiDriverStrategy.ApiResponse response = apiDriver.get("users.endpoint");
        Assert.assertEquals(response.getStatusCode(), 200);
    }
}
```

## 📁 Project Structure

```
taflex/
├── src/
│   ├── main/java/io/github/vinipx/taflex/
│   │   ├── core/
│   │   │   ├── config/      # ConfigManager & properties loading
│   │   │   ├── drivers/     # Strategy implementations (Web, API, Mobile)
│   │   │   ├── locators/    # Hierarchical locator resolution
│   │   │   └── utils/       # XrayService, CapabilityBuilder
│   │   └── database/        # DatabaseManager & Connection Pool
│   └── test/java/io/github/vinipx/taflex/
│       ├── base/            # BaseTest lifecycle
│       ├── listeners/       # TestNG reporting & retry logic
│       └── tests/           # Web, API, and Mobile suites
├── documentation/           # Docusaurus documentation site
├── setup.sh                 # Environment initialization
└── automation.properties    # Central configuration
```

## 🛡️ Code Quality & CI

TAFLEX maintains high standards through automated hygiene checks and robust CI integration:

- **Checkstyle**: Enforces coding standards (naming, indentation, whitespace).
- **PMD**: Detects code smells, dead code, and potential programming errors.
- **CI/CD Ready**: Fully configured pipelines for **GitHub Actions** and **GitLab CI**.

Run local verification:
```bash
./gradlew verifyBuild test
```

## 📊 Governance & Reporting

- **Xray Integration**: Link tests to Jira issues. Configure `xray.client.id` and `xray.client.secret` in properties.
- **ReportPortal**: AI-powered dashboard. Enable via `reportportal.enabled=true`.
- **Local Reports**: Standard TestNG reports are generated at `build/reports/tests/index.html`.
- **Screenshots**: Automatically captured on failure in the `screenshots/` directory.

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](documentation/docs/contributing/guidelines.md) for details.

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

---

**Happy Testing! 🚀**

<div align="center">
Built with ❤️ by <a href="https://github.com/vinipx">vinipx</a> and the TAFLEX Community.
</div>
