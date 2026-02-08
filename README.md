# TAFLEX - Unified Test Automation Framework

<p align="center">
  <img src="docs/assets/logo.svg" alt="TAFLEX Logo" width="320"/>
</p>

[![Java Version](https://img.shields.io/badge/Java-21%2B-blue.svg)](https://openjdk.org/)
[![Gradle](https://img.shields.io/badge/Gradle-8.5-green.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](LICENSE)
[![GitHub Repo](https://img.shields.io/badge/GitHub-vinipx/taflex-blue.svg)](https://github.com/vinipx/taflex)

A production-ready, enterprise-grade unified test automation framework supporting **Web** (Playwright), **API** (Apache HttpClient), and **Mobile** (Appium) testing with externalized locators and configuration.

## 🎯 Key Features

- **Unified Architecture**: Single codebase for Web, API, and Mobile testing
- **Strategy Pattern**: Runtime driver resolution based on configuration
- **Externalized Locators**: All selectors in `.properties` files, decoupled from code
- **Java 21+**: Modern Java with latest LTS features
- **TestNG**: Mature test runner with parallel execution support
- **ReportPortal**: Native integration for centralized reporting
- **Database Support**: HikariCP connection pooling with JDBC wrapper
- **Retry Mechanism**: Automatic retry for flaky tests
- **Screenshot Capture**: Automatic screenshots on test failure

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
│  (Play-     │ │ (Http-     │ │  (Appium)  │
│  wright)    │ │  Client)   │ │            │
└─────────────┘ └────────────┘ └────────────┘
           │             │              │
┌──────────▼─────────────▼──────────────▼─────────────┐
│              Externalized Locators                  │
│         (PropertiesLocatorStrategy)                 │
└─────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Java 21+** - [Download here](https://adoptium.net/)
- **Gradle 8.5+** - Or use included wrapper
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

This will:
- Check Java version (requires 21+)
- Verify Gradle installation
- Create `automation.properties` from template
- Create required directories
- Resolve dependencies
- Compile test sources

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

# Run smoke tests
./gradlew smokeTest

# Run regression tests
./gradlew regressionTest

# Run with specific suite
./gradlew test -Psuite=web-suite
```

## ⚙️ Configuration

### Execution Modes

Set in `automation.properties`:

```properties
# Mode: web | api | mobile
execution.mode=web
```

### Web Configuration

```properties
web.browser=chromium        # chromium | firefox | webkit
web.headless=false
web.timeout=30
web.base.url=https://staging.example.com
```

### API Configuration

```properties
api.base.url=https://api.staging.example.com
api.timeout=30
api.content.type=application/json
```

### Mobile Configuration

```properties
mobile.platform=android
mobile.appium.url=http://localhost:4723
mobile.device.name=Pixel5
mobile.app.path=/path/to/app.apk
```

## 📝 Writing Tests

### Web Test Example

```java
package io.github.vinipx.taflex.tests.web;

import io.github.vinipx.taflex.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {
    
    @Test(groups = {"smoke"})
    public void shouldLoginSuccessfully() {
        // Navigate using externalized URL locator
        driver.navigateTo("login.page.url");
        
        // Use externalized locators
        driver.type("login.username.field", "testuser");
        driver.type("login.password.field", "password123");
        driver.click("login.submit.button");
        
        // Assertions
        Assert.assertTrue(driver.isVisible("dashboard.welcome.message"));
    }
}
```

### API Test Example

```java
package io.github.vinipx.taflex.tests.api;

import io.github.vinipx.taflex.base.BaseTest;
import io.github.vinipx.taflex.core.drivers.strategies.ApiDriverStrategy;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserApiTests extends BaseTest {
    
    @Test(groups = {"smoke"})
    public void shouldCreateUser() {
        ApiDriverStrategy apiDriver = (ApiDriverStrategy) driver;
        
        String payload = "{\"name\": \"Test User\", \"email\": \"test@example.com\"}";
        ApiDriverStrategy.ApiResponse response = apiDriver.post("user.create.endpoint", payload);
        
        Assert.assertEquals(response.getStatusCode(), 201);
    }
}
```

## 📁 Project Structure

```
taflex/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/enterprise/taflex/
│   │           ├── core/
│   │           │   ├── drivers/          # Driver interfaces & implementations
│   │           │   ├── locators/         # Locator strategies
│   │           │   ├── config/           # Configuration management
│   │           │   └── exceptions/       # Custom exceptions
│   │           ├── database/             # Database utilities
│   │           └── utils/                # Utility classes
│   └── test/
│       ├── java/
│       │   └── com/enterprise/taflex/
│       │       ├── base/                 # Base test class
│       │       ├── listeners/            # TestNG listeners
│       │       └── tests/                # Test classes
│       │           ├── web/
│       │           ├── api/
│       │           └── mobile/
│       └── resources/
│           ├── locators/                 # Externalized locators
│           │   ├── global.properties
│           │   ├── web/
│           │   ├── api/
│           │   └── mobile/
│           ├── testng/                   # TestNG suite files
│           └── data/                     # Test data files
├── build.gradle                          # Gradle build configuration
├── setup.sh                              # Setup script
├── automation.properties.template         # Configuration template
└── README.md                             # This file
```

## 🔍 Locator Management

All locators are externalized in `.properties` files:

**File Structure**:
```
src/test/resources/locators/
├── global.properties          # Common across all modes
├── web/
│   ├── common.properties      # Web-specific common locators
│   └── *.properties           # Page-specific locators
├── api/
│   └── endpoints.properties   # API endpoints
└── mobile/
    └── selectors.properties   # Mobile selectors
```

**Example Locator File** (`web/common.properties`):
```properties
# Page URLs
login.page.url=https://staging.example.com/login
dashboard.page.url=https://staging.example.com/dashboard

# Login Page Elements
login.username.field=#username
login.password.field=#password
login.submit.button=button[type='submit']
```

**Using Locators in Tests**:
```java
// The framework automatically resolves "login.username.field" to "#username"
driver.type("login.username.field", "testuser");
```

## 📊 Reporting

### Report Portal Integration

1. Enable in `automation.properties`:
```properties
reportportal.enabled=true
reportportal.endpoint=http://localhost:8080
reportportal.api.key=your-api-key
reportportal.project=test_automation
```

2. Run tests - results will be automatically reported

### Local Reports

- **TestNG Reports**: `build/reports/tests/`
- **Screenshots**: `screenshots/`
- **Logs**: `logs/test-automation.log`

## 🧪 Test Groups

Tests are organized by groups for flexible execution:

- **smoke**: Quick health checks
- **regression**: Full test suite
- **web**: Web-specific tests
- **api**: API-specific tests
- **mobile**: Mobile-specific tests

Run specific groups:
```bash
./gradlew test -Dgroups=smoke
./gradlew test -Dgroups=regression
```

## 🔧 Advanced Features

### Retry Mechanism

Failed tests are automatically retried (configurable):
```properties
retry.enabled=true
retry.max.attempts=2
```

### Parallel Execution

```properties
parallel.enabled=true
parallel.threads=4
```

### Database Integration

```java
DatabaseManager db = DatabaseManager.getInstance();
List<Map<String, Object>> results = db.executeQuery(
    "SELECT * FROM users WHERE status = ?", "active"
);
```

## 🐛 Debugging

### Enable Debug Logging

```properties
log.level=DEBUG
```

### View Test Logs

```bash
tail -f logs/test-automation.log
```

### Screenshots on Failure

Automatic screenshots are captured when tests fail:
```properties
screenshot.on.failure=true
```

## 📚 Documentation

- **Docs site**: https://vinipx.github.io/taflex
- [Architecture Overview](docs/architecture/overview.md)
- [API Reference](docs/api/core-interfaces.md)
- [Best Practices](docs/best-practices/test-design.md)
- [Troubleshooting](docs/troubleshooting/common-issues.md)

## 🤖 AI Cursor Configuration

This repo includes optional Cursor guidance to keep contributions consistent.

- **Rules**: `.cursor/rules/` defines framework standards and conventions.
- **Skills**: `.cursor/skills/` provides task-specific guidance (API, web, mobile).
- **Usage**: open the project in Cursor and these are auto‑loaded.

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Microsoft Playwright](https://playwright.dev/java/)
- [TestNG](https://testng.org/)
- [Appium](http://appium.io/)
- [ReportPortal](https://reportportal.io/)

---

**Happy Testing! 🚀**