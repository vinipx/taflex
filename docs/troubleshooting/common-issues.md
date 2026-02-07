# Common Issues & Troubleshooting

This guide helps you resolve common issues encountered when using TAFLEX.

## Quick Diagnostics

Before diving into specific issues, run these diagnostic commands:

```bash
# Check Java version
java -version

# Verify Gradle installation
./gradlew --version

# Test compilation
./gradlew compileJava --info

# Check configuration
cat automation.properties | grep -v "^#" | grep -v "^$"
```

## Installation Issues

### Issue: Java Version Not Found

**Symptoms**:
```
✗ Java 23 or higher required. Found: 17.0.10
```

**Solutions**:

=== "Install Java 23"

    Download from [Adoptium](https://adoptium.net/):
    ```bash
    # macOS (using Homebrew)
    brew install --cask temurin
    
    # Or download manually and set JAVA_HOME
    export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-23.jdk/Contents/Home
    export PATH=$JAVA_HOME/bin:$PATH
    ```

=== "Use Existing Java 23"

    If already installed but not detected:
    ```bash
    # Find Java installations
    /usr/libexec/java_home -V
    
    # Set JAVA_HOME to Java 23
    export JAVA_HOME=$(/usr/libexec/java_home -v 23)
    export PATH=$JAVA_HOME/bin:$PATH
    ```

=== "Verify Installation"

    ```bash
    java -version
    # Should show: openjdk version "23.0.x"
    ```

### Issue: Gradle Permission Denied

**Symptoms**:
```
bash: ./gradlew: Permission denied
```

**Solution**:
```bash
chmod +x gradlew
chmod +x setup.sh
```

### Issue: Setup Script Fails

**Symptoms**:
```
✗ Dependencies resolved successfully
✗ Compilation failed
```

**Debug Steps**:

1. Check detailed error:
   ```bash
   ./gradlew compileTestJava --info 2>&1 | tail -100
   ```

2. Common fixes:
   ```bash
   # Clean and rebuild
   ./gradlew clean build
   
   # Refresh dependencies
   ./gradlew --refresh-dependencies
   
   # Clear Gradle cache
   rm -rf ~/.gradle/caches/
   ```

## Configuration Issues

### Issue: Configuration File Not Found

**Symptoms**:
```
Configuration file automation.properties not found
```

**Solution**:
```bash
# Create from template
cp automation.properties.template automation.properties

# Or manually create
touch automation.properties
```

### Issue: Locator Not Found

**Symptoms**:
```
LocatorException: Locator 'login.button' not found in any properties file
```

**Checklist**:

1. :white_check_mark: File exists in correct location:
   ```
   src/test/resources/locators/web/common.properties
   ```

2. :white_check_mark: Key is spelled correctly:
   ```properties
   login.button=#submit  # ✓
   login.buton=#submit   # ✗ Typo
   ```

3. :white_check_mark: File is saved and not open in editor

4. :white_check_mark: Correct execution mode:
   ```properties
   execution.mode=web  # Matches locators/web/ folder
   ```

### Issue: Invalid Configuration Value

**Symptoms**:
```
NumberFormatException: For input string: "thirty"
```

**Common Mistakes**:

| Wrong | Correct |
|-------|---------|
| `web.timeout=thirty` | `web.timeout=30` |
| `web.headless=yes` | `web.headless=true` |
| `parallel.threads=4;` | `parallel.threads=4` |

## Test Execution Issues

### Issue: Browser Not Launching (Web Tests)

**Symptoms**:
```
TimeoutException: Browser.launch() timed out after 30000ms
```

**Solutions**:

=== "Install Playwright Browsers"

    ```bash
    ./gradlew installPlaywright
    
    # Or manually
    npx playwright install
    ```

=== "Check Browser Path"

    ```bash
    # Verify Chromium is installed
    ls ~/Library/Caches/ms-playwright/chromium-*/chrome-mac/Chromium.app
    
    # Reinstall if missing
    ./gradlew installPlaywright
    ```

=== "Use Different Browser"

    ```properties
    # Try Firefox
    web.browser=firefox
    
    # Or WebKit
    web.browser=webkit
    ```

### Issue: API Tests Return 404

**Symptoms**:
```
AssertionError: expected [201] but found [404]
```

**Debug Steps**:

1. Verify base URL:
   ```properties
   api.base.url=https://api.example.com  # No trailing slash
   ```

2. Check endpoint in locators:
   ```properties
   users.endpoint=/api/v1/users  # Leading slash required
   ```

3. Test manually:
   ```bash
   curl -v https://api.example.com/api/v1/users
   ```

### Issue: Appium Connection Refused

**Symptoms**:
```
WebDriverException: Connection refused (Connection refused)
```

**Solutions**:

1. Start Appium server:
   ```bash
   appium
   ```

2. Check Appium configuration:
   ```properties
   mobile.appium.url=http://localhost:4723
   ```

3. Verify device connection:
   ```bash
   # Android
   adb devices
   
   # iOS
   ios-deploy -c
   ```

## Compilation Issues

### Issue: Class Not Found

**Symptoms**:
```
error: cannot find symbol: class AutomationDriver
```

**Solutions**:

1. Check imports:
   ```java
   import io.github.vinipx.taflex.core.drivers.AutomationDriver;
   ```

2. Rebuild project:
   ```bash
   ./gradlew clean build
   ```

3. Check IDE configuration (IntelliJ):
   - File → Invalidate Caches / Restart

### Issue: Deprecated API Warnings

**Symptoms**:
```
Note: MobileDriverStrategy.java uses or overrides a deprecated API
```

**Solution**:
This is usually safe to ignore, but you can suppress:

```java
@SuppressWarnings("deprecation")
public void oldMethod() {
    // deprecated API usage
}
```

Or upgrade the dependency to a newer version in `build.gradle`.

## Runtime Issues

### Issue: Element Not Interactable

**Symptoms**:
```
ElementClickInterceptedException: element click intercepted
```

**Solutions**:

1. Wait for element:
   ```java
   driver.waitForVisible("element.name", 10);
   ```

2. Scroll into view:
   ```java
   Element element = driver.findElement("element.name");
   element.scrollIntoView();
   element.click();
   ```

3. Use JavaScript click:
   ```java
   // In Playwright
   page.evaluate("document.querySelector('selector').click()");
   ```

### Issue: Stale Element Reference

**Symptoms**:
```
StaleElementReferenceException: stale element reference
```

**Cause**: DOM changed after finding element

**Solution**:
```java
// Refind element before each action
for (int i = 0; i < 3; i++) {
    try {
        driver.click("dynamic.element");
        break;
    } catch (StaleElementReferenceException e) {
        Thread.sleep(500);
    }
}
```

### Issue: Timeout Exceptions

**Symptoms**:
```
TimeoutException: Expected condition failed
```

**Solutions**:

1. Increase timeout:
   ```properties
   web.timeout=60
   ```

2. Check element exists:
   ```bash
   # Verify locator is correct
   # Check if element is actually on page
   ```

3. Add explicit wait:
   ```java
   driver.waitForVisible("slow.loading.element", 30);
   ```

## Database Issues

### Issue: Connection Pool Exhausted

**Symptoms**:
```
SQLException: Connection pool is exhausted
```

**Solutions**:

1. Increase pool size:
   ```properties
   db.pool.size=20
   ```

2. Close connections properly:
   ```java
   try (Connection conn = DatabaseManager.getInstance().getConnection()) {
       // use connection
   }  // Auto-closes
   ```

3. Check for connection leaks:
   ```java
   // Always close in finally block or use try-with-resources
   ```

### Issue: Database Not Available

**Symptoms**:
```
SQLException: No suitable driver found
```

**Solutions**:

1. Check JDBC URL:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/testdb
   ```

2. Verify driver dependency in `build.gradle`:
   ```groovy
   runtimeOnly 'org.postgresql:postgresql:42.7.1'
   ```

3. Test connection:
   ```bash
   psql -h localhost -U username -d testdb
   ```

## Performance Issues

### Issue: Slow Test Execution

**Diagnose**:
```bash
# Profile test execution
./gradlew test --profile

# Generate build scan
./gradlew test --scan
```

**Solutions**:

1. Enable parallel execution:
   ```properties
   parallel.enabled=true
   parallel.threads=8
   ```

2. Optimize locators:
   - Use CSS selectors over XPath
   - Avoid complex selectors

3. Reuse driver instances:
   ```java
   @BeforeSuite
   public void globalSetup() {
       // Initialize once
   }
   ```

### Issue: Memory Leaks

**Symptoms**:
```
OutOfMemoryError: Java heap space
```

**Solutions**:

1. Increase heap size:
   ```bash
   export GRADLE_OPTS="-Xmx4g -XX:MaxMetaspaceSize=512m"
   ```

2. Terminate drivers properly:
   ```java
   @AfterMethod
   public void tearDown() {
       if (driver != null) {
           driver.terminate();
       }
   }
   ```

3. Clear caches:
   ```java
   @AfterSuite
   public void cleanup() {
       DriverFactory.clearCache();
       DatabaseManager.closePool();
   }
   ```

## Reporting Issues

### Issue: ReportPortal Not Receiving Data

**Symptoms**:
- Tests run but no reports in ReportPortal

**Solutions**:

1. Check configuration:
   ```properties
   reportportal.enabled=true
   reportportal.endpoint=http://localhost:8080
   reportportal.api.key=your-api-key
   ```

2. Verify ReportPortal is running:
   ```bash
   docker ps | grep reportportal
   ```

3. Check firewall/network:
   ```bash
   curl http://localhost:8080
   ```

### Issue: Screenshots Not Captured

**Symptoms**:
- No screenshots in `screenshots/` folder

**Solutions**:

1. Enable screenshots:
   ```properties
   screenshot.on.failure=true
   ```

2. Check directory permissions:
   ```bash
   mkdir -p screenshots
   chmod 755 screenshots
   ```

3. Verify path in code:
   ```java
   String path = driver.captureScreenshot("test");
   System.out.println("Screenshot saved to: " + path);
   ```

## CI/CD Issues

### Issue: Tests Pass Locally But Fail in CI

**Common Causes**:

1. **Different environments**:
   ```bash
   # Check environment variables
   env | grep -i java
   ```

2. **Timing issues**:
   - CI is slower → Add more waits
   - Race conditions → Add synchronization

3. **Missing dependencies**:
   ```yaml
   # In CI config, ensure all services are ready
   - name: Wait for services
     run: sleep 30
   ```

### Issue: Gradle Daemon Issues

**Symptoms**:
```
Gradle build daemon disappeared unexpectedly
```

**Solutions**:

1. Stop daemon:
   ```bash
   ./gradlew --stop
   ```

2. Run without daemon:
   ```bash
   ./gradlew test --no-daemon
   ```

3. Clear daemon files:
   ```bash
   rm -rf ~/.gradle/daemon/
   ```

## Getting Help

If you can't resolve an issue:

1. **Check logs**:
   ```bash
   cat logs/test-automation.log
   cat build/reports/tests/*.html
   ```

2. **Enable debug mode**:
   ```properties
   log.level=DEBUG
   ```

3. **Create minimal reproduction**:
   - Single test that fails
   - Minimal configuration
   - Clear error message

4. **Contact support**:
   - :fontawesome-brands-slack: Slack: #taflex-support
   - :fontawesome-brands-github: GitHub Issues
   - :fontawesome-solid-envelope: Email: support@taflex.io

## FAQ

**Q: Can I use TAFLEX with multiple Java versions?**  
A: No, TAFLEX requires Java 23+. Use `jenv` or similar tools to manage versions.

**Q: How do I run a single test method?**  
A: `./gradlew test --tests ClassName.methodName`

**Q: Can I use TAFLEX without Gradle?**  
A: Gradle is the recommended build tool, but you could adapt it to Maven.

**Q: Is there a Docker image available?**  
A: Yes, see the [DevOps Guide](../guides/devops.md) for Docker setup.

**Q: How do I contribute to TAFLEX?**  
A: See the [Contributing Guide](../contributing/guidelines.md).

---

**Still stuck?** Create an issue with:
- Full error message
- Steps to reproduce
- Environment details (OS, Java version, etc.)
- Relevant configuration files (redact sensitive info)