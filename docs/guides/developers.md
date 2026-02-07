# Guide for Developers

This guide is for software developers who want to extend TAFLEX, create new test cases, or integrate the framework into their development workflow.

## Who This Guide Is For

- :material-code-tags: Java developers writing test code
- :material-git: SDETs (Software Development Engineers in Test)
- :material-strategy: Architects designing test frameworks
- :material-auto-fix: Developers maintaining test infrastructure

## Developer Responsibilities

As a developer working with TAFLEX, you'll:

<div class="grid cards" markdown>

-   :material-code-tags:{ .lg .middle } **Write Test Code**

    ---

    Implement test classes using the framework's APIs and patterns.

-   :material-wrench:{ .lg .middle } **Extend Framework**

    ---

    Add new driver strategies, utilities, and custom functionality.

-   :material-bug:{ .lg .middle } **Debug & Optimize**

    ---

    Troubleshoot failures, improve performance, and fix issues.

-   :material-git:{ .lg .middle } **Maintain Code**

    ---

    Refactor, review code, and ensure code quality standards.

</div>

## Development Environment Setup

### 1. IDE Configuration (IntelliJ IDEA)

#### Import Project

```bash
# Clone repository
git clone https://github.com/your-org/taflex.git
cd taflex

# Open in IntelliJ
idea .
```

#### Recommended Plugins

- **Lombok** - For cleaner code
- **Rainbow Brackets** - Better bracket visibility
- **TestNG** - Test runner integration
- **Material Theme UI** - Better UI theme

#### IDE Settings

```xml title=".idea/codeStyles/Project.xml"
<code_scheme name="TAFLEX" version="173">
  <JavaCodeStyleSettings>
    <option name="CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND" value="99" />
    <option name="NAMES_COUNT_TO_USE_IMPORT_ON_DEMAND" value="99" />
    <option name="IMPORT_LAYOUT_TABLE">
      <value>
        <package name="" withSubpackages="true" static="true" />
        <emptyLine />
        <package name="java" withSubpackages="true" static="false" />
        <package name="javax" withSubpackages="true" static="false" />
        <emptyLine />
        <package name="" withSubpackages="true" static="false" />
        <emptyLine />
        <package name="io.github.vinipx" withSubpackages="true" static="false" />
      </value>
    </option>
  </JavaCodeStyleSettings>
</code_scheme>
```

### 2. Project Structure Deep Dive

```
taflex/
├── src/
│   ├── main/java/com/enterprise/taflex/  ← Framework Code
│   │   ├── core/
│   │   │   ├── drivers/          # Driver implementations
│   │   │   ├── locators/         # Locator strategies
│   │   │   ├── config/           # Configuration management
│   │   │   └── exceptions/       # Custom exceptions
│   │   ├── database/             # Database utilities
│   │   └── utils/                # Helper classes
│   │
│   └── test/java/com/enterprise/taflex/  ← Test Code
│       ├── base/                 # Base test classes
│       ├── listeners/            # TestNG listeners
│       └── tests/                # Test implementations
│           ├── web/              # Web tests
│           ├── api/              # API tests
│           └── mobile/           # Mobile tests
│
├── src/test/resources/           ← Test Resources
│   ├── locators/                 # .properties files
│   ├── testng/                   # Suite files
│   └── data/                     # Test data
│
└── docs/                         # Documentation
```

## Writing Tests

### Test Class Structure

```java
package io.github.vinipx.taflex.tests.web;

import io.github.vinipx.taflex.base.BaseTest;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Login functionality tests.
 * 
 * @author Your Name
 * @since 1.0.0
 */
public class LoginTests extends BaseTest {
    
    @Test(
        groups = {"smoke", "regression"},
        description = "Verify successful login with valid credentials",
        priority = 1
    )
    public void shouldLoginSuccessfully() {
        // Given
        String username = "validuser";
        String password = "validpass";
        
        // When
        driver.navigateTo("login.page.url");
        driver.type("login.username.field", username);
        driver.type("login.password.field", password);
        driver.click("login.submit.button");
        
        // Then
        assertThat(driver.isVisible("dashboard.welcome.message"))
            .as("User should be redirected to dashboard after login")
            .isTrue();
        
        assertThat(driver.getText("dashboard.user.name"))
            .as("Dashboard should display username")
            .contains(username);
    }
}
```

### Using Data Providers

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
public void shouldHandleLoginScenarios(
        String username, 
        String password, 
        boolean shouldSucceed) {
    
    driver.navigateTo("login.page.url");
    driver.type("login.username.field", username);
    driver.type("login.password.field", password);
    driver.click("login.submit.button");
    
    if (shouldSucceed) {
        assertThat(driver.isVisible("dashboard.welcome.message")).isTrue();
    } else {
        assertThat(driver.isVisible("login.error.message")).isTrue();
    }
}
```

### Page Object Pattern (Optional)

While TAFLEX supports externalized locators, you can also use Page Objects:

```java
public class LoginPage {
    private final AutomationDriver driver;
    
    public LoginPage(AutomationDriver driver) {
        this.driver = driver;
    }
    
    public LoginPage navigate() {
        driver.navigateTo("login.page.url");
        return this;
    }
    
    public LoginPage enterCredentials(String username, String password) {
        driver.type("login.username.field", username);
        driver.type("login.password.field", password);
        return this;
    }
    
    public DashboardPage clickLogin() {
        driver.click("login.submit.button");
        return new DashboardPage(driver);
    }
}

// Usage in test
@Test
public void shouldLoginWithPageObject() {
    DashboardPage dashboard = new LoginPage(driver)
        .navigate()
        .enterCredentials("user", "pass")
        .clickLogin();
    
    assertThat(dashboard.isLoaded()).isTrue();
}
```

## Extending the Framework

### Creating a Custom Driver Strategy

```java
package io.github.vinipx.taflex.core.drivers.strategies;

import io.github.vinipx.taflex.core.drivers.AutomationDriver;
import io.github.vinipx.taflex.core.drivers.elements.Element;

/**
 * Custom driver for desktop application testing using WinAppDriver.
 */
public class DesktopDriverStrategy implements AutomationDriver {
    
    private WindowsDriver windowsDriver;
    private LocatorStrategy locatorStrategy;
    
    @Override
    public void initialize() {
        // Initialize WinAppDriver
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("app", ConfigManager.getProperty("desktop.app.path"));
        
        windowsDriver = new WindowsDriver(
            new URL(ConfigManager.getProperty("winappdriver.url")),
            capabilities
        );
        
        locatorStrategy = LocatorFactory.getLocatorStrategy();
    }
    
    @Override
    public void click(String logicalName) {
        String selector = locatorStrategy.resolve(logicalName);
        windowsDriver.findElement(By.name(selector)).click();
    }
    
    // ... implement other methods
}
```

Register in `DriverFactory`:

```java
public static AutomationDriver getDriver(String mode) {
    switch (mode.toLowerCase()) {
        case "web": return new PlaywrightDriverStrategy();
        case "api": return new ApiDriverStrategy();
        case "mobile": return new MobileDriverStrategy();
        case "desktop": return new DesktopDriverStrategy(); // Add this
        default: throw new DriverException("Unknown mode: " + mode);
    }
}
```

### Creating Custom Test Listeners

```java
package io.github.vinipx.taflex.listeners;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * Custom listener that sends notifications to Slack.
 */
public class SlackNotificationListener extends TestListenerAdapter {
    
    private SlackClient slackClient;
    
    @Override
    public void onTestFailure(ITestResult result) {
        String message = String.format(
            "Test Failed: %s.%s\nError: %s",
            result.getTestClass().getRealClass().getSimpleName(),
            result.getName(),
            result.getThrowable().getMessage()
        );
        
        slackClient.sendMessage("#qa-alerts", message);
    }
}
```

Add to `testng.xml`:

```xml
<listeners>
    <listener class-name="io.github.vinipx.taflex.listeners.SlackNotificationListener"/>
</listeners>
```

## Working with the Database

### Query Execution

```java
DatabaseManager db = DatabaseManager.getInstance();

// Simple query
List<Map<String, Object>> users = db.executeQuery(
    "SELECT * FROM users WHERE status = ?", 
    "active"
);

// Transaction
Boolean result = db.executeInTransaction(conn -> {
    PreparedStatement stmt1 = conn.prepareStatement(
        "UPDATE users SET last_login = NOW() WHERE id = ?"
    );
    stmt1.setInt(1, userId);
    stmt1.executeUpdate();
    
    PreparedStatement stmt2 = conn.prepareStatement(
        "INSERT INTO audit_log (action, user_id) VALUES (?, ?)"
    );
    stmt2.setString(1, "LOGIN");
    stmt2.setInt(2, userId);
    stmt2.executeUpdate();
    
    return true;
});
```

### Test Data Setup/Teardown

```java
@BeforeMethod
public void setUpTestData() {
    DatabaseManager db = DatabaseManager.getInstance();
    
    // Create test user
    db.executeUpdate(
        "INSERT INTO users (username, email, status) VALUES (?, ?, ?)",
        "testuser", "test@example.com", "active"
    );
}

@AfterMethod
public void tearDownTestData() {
    DatabaseManager db = DatabaseManager.getInstance();
    
    // Clean up test data
    db.executeUpdate(
        "DELETE FROM users WHERE username = ?",
        "testuser"
    );
}
```

## Debugging Tips

### 1. Enable Debug Logging

```properties
log.level=DEBUG
```

### 2. Run Single Test with Debug

```bash
./gradlew test --tests LoginTests.shouldLoginSuccessfully --info
```

### 3. Use Breakpoints

Set breakpoints in your IDE and run with debugger:

```bash
./gradlew test --tests LoginTests --debug-jvm
```

Then attach debugger on port 5005.

### 4. View Playwright Trace

```java
// Enable tracing in config
web.tracing.enabled=true
```

After test failure, view trace:

```bash
npx playwright show-trace traces/trace.zip
```

### 5. Screenshot Debugging

```java
// Take screenshot at specific point
driver.captureScreenshot("before-click");
driver.click("button");
driver.captureScreenshot("after-click");
```

## Code Quality

### Static Analysis

Add SpotBugs to `build.gradle`:

```groovy
plugins {
    id 'com.github.spotbugs' version '5.0.14'
}

spotbugs {
    toolVersion = '4.7.3'
    effort = com.github.spotbugs.snom.Effort.MAX
    reportLevel = com.github.spotbugs.snom.Confidence.LOW
}
```

### Code Coverage

```groovy
plugins {
    id 'jacoco'
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}
```

Generate report:

```bash
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

## CI/CD Integration

### GitHub Actions

```yaml title=".github/workflows/test.yml"
name: Run Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 23
      uses: actions/setup-java@v3
      with:
        java-version: '23'
        distribution: 'corretto'
    
    - name: Setup Gradle
      uses: gradle/gradle-build-action@v2
    
    - name: Create config
      run: cp automation.properties.template automation.properties
    
    - name: Run tests
      run: ./gradlew smokeTest
    
    - name: Upload reports
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-reports
        path: build/reports/
```

### Jenkins Pipeline

```groovy
pipeline {
    agent any
    
    tools {
        jdk 'JDK23'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Setup') {
            steps {
                sh './setup.sh'
            }
        }
        
        stage('Test') {
            steps {
                sh './gradlew smokeTest'
            }
        }
    }
    
    post {
        always {
            publishTestResults testResultsPattern: 'build/test-results/**/*.xml'
            archiveArtifacts artifacts: 'build/reports/**/*', allowEmptyArchive: true
        }
    }
}
```

## Performance Optimization

### 1. Parallel Execution

```properties
parallel.enabled=true
parallel.threads=8
```

### 2. Driver Reuse

```java
// Cache driver instances
@BeforeSuite
public void globalSetup() {
    // Initialize shared resources
}

@AfterSuite
public void globalTeardown() {
    DriverFactory.clearCache();
}
```

### 3. Database Connection Pooling

```properties
db.pool.size=20
db.pool.minIdle=5
db.pool.maxLifetime=1800000
```

## Common Development Patterns

### Retry Logic

```java
@Test(retryAnalyzer = RetryAnalyzer.class)
public void flakyTest() {
    // Test that occasionally fails due to timing
}
```

### Soft Assertions

```java
@Test
public void validatePage() {
    SoftAssertions softly = new SoftAssertions();
    
    softly.assertThat(driver.isVisible("header")).isTrue();
    softly.assertThat(driver.isVisible("footer")).isTrue();
    softly.assertThat(driver.isVisible("sidebar")).isTrue();
    
    softly.assertAll(); // Reports all failures at once
}
```

### Conditional Tests

```java
@Test
public void mobileOnlyTest() {
    assumeTrue(ConfigManager.getExecutionMode().equals("mobile"));
    
    // Mobile-specific test logic
}
```

## Resources

- :material-book: [Effective Java](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)
- :material-test-tube: [TestNG Documentation](https://testng.org/doc/)
- :material-git: [Git Best Practices](https://www.git-scm.com/doc)
- :material-code-tags: [Java 23 Features](https://openjdk.org/projects/jdk/23/)

---

**Pro Tip**: Always run the full test suite before committing changes. Use `./gradlew smokeTest` for quick validation and `./gradlew regressionTest` for comprehensive testing!