---
sidebar_position: 1
title: Common Issues
---

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

**Symptoms:**
```
✗ Java 23 or higher required. Found: 17.0.10
```

**Solutions:**

Download from [Adoptium](https://adoptium.net/):

```bash
# macOS (using Homebrew)
brew install --cask temurin

# Or download manually and set JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-23.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

Verify installation:

```bash
java -version
# Should show: openjdk version "23.0.x"
```

### Issue: Gradle Permission Denied

**Symptoms:**
```
bash: ./gradlew: Permission denied
```

**Solution:**
```bash
chmod +x gradlew
chmod +x setup.sh
```

### Issue: Setup Script Fails

**Symptoms:**
```
✗ Dependencies resolved successfully
✗ Compilation failed
```

**Debug Steps:**

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

**Symptoms:**
```
Configuration file automation.properties not found
```

**Solution:**
```bash
# Create from template
cp automation.properties.template automation.properties

# Or manually create
touch automation.properties
```

### Issue: Locator Not Found

**Symptoms:**
```
LocatorException: Locator 'login.button' not found in any properties file
```

**Checklist:**

1. ✅ File exists in correct location:
   ```
   src/test/resources/locators/web/common.properties
   ```

2. ✅ Key is spelled correctly:
   ```properties
   login.button=#submit  # ✓
   login.buton=#submit   # ✗ Typo
   ```

3. ✅ File is saved and not open in editor

4. ✅ Correct execution mode:
   ```properties
   execution.mode=web  # Matches locators/web/ folder
   ```

### Issue: Invalid Configuration Value

**Symptoms:**
```
NumberFormatException: For input string: "thirty"
```

**Common Mistakes:**

| Wrong | Correct |
|-------|---------|
| `web.timeout=thirty` | `web.timeout=30` |
| `web.headless=yes` | `web.headless=true` |
| `parallel.threads=4;` | `parallel.threads=4` |

## Test Execution Issues

### Issue: Browser Not Launching (Web Tests)

**Symptoms:**
```
TimeoutException: Browser.launch() timed out after 30000ms
```

**Solutions:**

Install Playwright browsers:
```bash
./gradlew installPlaywright

# Or manually
npx playwright install
```

Try a different browser:
```properties
# Try Firefox
web.browser=firefox

# Or WebKit
web.browser=webkit
```

### Issue: API Tests Return 404

**Symptoms:**
```
AssertionError: expected [201] but found [404]
```

**Debug Steps:**

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

**Symptoms:**
```
WebDriverException: Connection refused (Connection refused)
```

**Solutions:**

1. Start Appium server:
   ```bash
   appium
   ```

2. Optionally enable auto-start:
   ```properties
   mobile.appium.auto.start=true
   mobile.appium.start.command=appium
   ```

3. Check Appium configuration:
   ```properties
   mobile.appium.url=http://localhost:4723
   ```

4. Verify device connection:
   ```bash
   # Android
   adb devices

   # iOS
   ios-deploy -c
   ```

## Runtime Issues

### Issue: Element Not Interactable

**Symptoms:**
```
ElementClickInterceptedException: element click intercepted
```

**Solutions:**

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

### Issue: Timeout Exceptions

**Symptoms:**
```
TimeoutException: Expected condition failed
```

**Solutions:**

1. Increase timeout:
   ```properties
   web.timeout=60
   ```

2. Add explicit wait:
   ```java
   driver.waitForVisible("slow.loading.element", 30);
   ```

## Database Issues

### Issue: Connection Pool Exhausted

**Symptoms:**
```
SQLException: Connection pool is exhausted
```

**Solutions:**

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

## Performance Issues

### Issue: Slow Test Execution

**Solutions:**

1. Enable parallel execution:
   ```properties
   parallel.enabled=true
   parallel.threads=8
   ```

2. Optimize locators — use CSS selectors over XPath, avoid complex selectors.

### Issue: Memory Leaks

**Symptoms:**
```
OutOfMemoryError: Java heap space
```

**Solutions:**

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

## Getting Help

If you can't resolve an issue:

1. **Check logs**: `cat logs/test-automation.log`
2. **Enable debug mode**: set `log.level=DEBUG`
3. **Create minimal reproduction**: Single test, minimal config, clear error message
4. **Contact support**: GitHub Issues or email

## FAQ

**Q: Can I use TAFLEX with multiple Java versions?**
A: No, TAFLEX requires Java 23+. Use `jenv` or similar tools to manage versions.

**Q: How do I run a single test method?**
A: `./gradlew test --tests ClassName.methodName`

**Q: Can I use TAFLEX without Gradle?**
A: Gradle is the recommended build tool, but you could adapt it to Maven.

**Q: How do I contribute to TAFLEX?**
A: See the [Contributing Guide](../contributing/guidelines.md).

---

**Still stuck?** Create an issue with full error message, steps to reproduce, and environment details.
