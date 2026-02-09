---
sidebar_position: 1
title: Core Interfaces
---

# API Reference

Complete API documentation for TAFLEX framework components.

## Core Interfaces

### AutomationDriver

The primary interface for all driver implementations.

```java
public interface AutomationDriver {

    /** Initialize the driver with configuration from automation.properties */
    void initialize();

    /** Clean up and terminate the driver */
    void terminate();

    /**
     * Get the native driver implementation
     * @return Native driver (Page for Playwright, CloseableHttpClient for API, AppiumDriver for Mobile)
     */
    <T> T getNativeDriver();

    /**
     * Navigate to a URL (for Web/Mobile)
     * @param urlKey Key from locators file containing the URL
     */
    void navigateTo(String urlKey);

    /**
     * Find an element using externalized locator
     * @param logicalName Logical name of the locator from properties file
     * @return Element wrapper
     */
    Element findElement(String logicalName);

    /**
     * Click on an element
     * @param logicalName Logical name of the locator from properties file
     */
    void click(String logicalName);

    /**
     * Type text into an element
     * @param logicalName Logical name of the locator from properties file
     * @param text Text to type
     */
    void type(String logicalName, String text);

    /**
     * Get text from an element
     * @param logicalName Logical name of the locator from properties file
     * @return Text content
     */
    String getText(String logicalName);

    /**
     * Check if element is visible
     * @param logicalName Logical name of the locator from properties file
     * @return true if visible
     */
    boolean isVisible(String logicalName);

    /**
     * Wait for element to be visible
     * @param logicalName Logical name of the locator from properties file
     * @param timeoutSeconds Timeout in seconds
     */
    void waitForVisible(String logicalName, int timeoutSeconds);

    /**
     * Capture screenshot
     * @param fileName Name for the screenshot file
     * @return Path to saved screenshot
     */
    String captureScreenshot(String fileName);

    /** Get the execution mode (web, api, mobile) */
    String getExecutionMode();
}
```

### Element

Unified interface for Web and Mobile elements.

```java
public interface Element {

    void click();
    void type(String text);
    void clear();
    String getText();
    boolean isVisible();
    boolean isEnabled();
    boolean isSelected();
    String getAttribute(String attributeName);
    void waitForVisible(int timeoutSeconds);
    void waitForClickable(int timeoutSeconds);
    <T> T getNativeElement();
    void scrollIntoView();
    void hover();
}
```

### LocatorStrategy

Strategy interface for resolving locators from external sources.

```java
public interface LocatorStrategy {

    /**
     * Resolve a logical locator name to an actual selector/path
     * @param logicalName The logical name (e.g., "login.username.field")
     * @return The resolved locator
     */
    String resolve(String logicalName);

    /**
     * Load locators from a source
     * @param sourcePath Path to the locator source
     */
    void load(String sourcePath);

    /** Check if a locator exists */
    boolean hasLocator(String logicalName);

    /** Reload locators */
    void reload();

    /** Get the source type */
    String getSourceType();
}
```

## Driver Factory

Factory class for creating driver instances.

```java
public class DriverFactory {

    /** Get driver based on execution mode from automation.properties */
    public static AutomationDriver getDriver();

    /** Get driver for specific execution mode */
    public static AutomationDriver getDriver(String mode);

    /** Get driver without caching (for parallel execution) */
    public static AutomationDriver getDriverWithoutCache(String mode);

    /** Clear driver cache */
    public static void clearCache();

    /** Remove specific driver from cache */
    public static void removeFromCache(String mode);
}
```

## Configuration API

### ConfigManager

Central configuration management.

```java
public class ConfigManager {

    public static String getProperty(String key);
    public static String getProperty(String key, String defaultValue);
    public static int getIntProperty(String key);
    public static int getIntProperty(String key, int defaultValue);
    public static boolean getBooleanProperty(String key);
    public static boolean getBooleanProperty(String key, boolean defaultValue);
    public static String getExecutionMode();
    public static boolean isHeadless();
    public static int getTimeout();
    public static void reload();
}
```

## Database API

### DatabaseManager

```java
public class DatabaseManager {

    /** Get singleton instance */
    public static synchronized DatabaseManager getInstance();

    /** Get connection from pool */
    public Connection getConnection();

    /** Execute SELECT query */
    public List<Map<String, Object>> executeQuery(String sql);

    /** Execute SELECT query with parameters */
    public List<Map<String, Object>> executeQuery(String sql, Object... params);

    /** Execute INSERT, UPDATE, DELETE */
    public int executeUpdate(String sql, Object... params);

    /** Execute query and return single value */
    public Object executeScalar(String sql, Object... params);

    /** Execute batch */
    public int[] executeBatch(String sql, List<Object[]> batchParams);

    /** Execute within transaction */
    public <T> T executeInTransaction(TransactionCallback<T> callback);

    /** Close connection pool */
    public static void closePool();

    /** Check if database is available */
    public boolean isAvailable();
}
```

## Usage Examples

### Basic Driver Usage

```java
// Initialize driver
AutomationDriver driver = DriverFactory.getDriver();
driver.initialize();

// Perform actions
driver.navigateTo("login.page.url");
driver.type("login.username.field", "user");
driver.type("login.password.field", "pass");
driver.click("login.submit.button");

// Verify
if (driver.isVisible("dashboard.welcome.message")) {
    System.out.println("Login successful!");
}

// Cleanup
driver.terminate();
```

### Database Operations

```java
DatabaseManager db = DatabaseManager.getInstance();

// Query
List<Map<String, Object>> users = db.executeQuery(
    "SELECT * FROM users WHERE status = ?",
    "active"
);

// Update
int rowsAffected = db.executeUpdate(
    "UPDATE users SET last_login = NOW() WHERE id = ?",
    userId
);

// Transaction
db.executeInTransaction(conn -> {
    // Multiple operations
    return result;
});
```

### Configuration Access

```java
// Get configuration values
String browser = ConfigManager.getProperty("web.browser", "chromium");
int timeout = ConfigManager.getIntProperty("web.timeout", 30);
boolean headless = ConfigManager.isHeadless();
```

## Exception Handling

### DriverException

```java
try {
    driver.click("nonexistent.element");
} catch (DriverException e) {
    logger.error("Driver error: {}", e.getMessage());
}
```

### LocatorException

```java
try {
    String selector = locatorStrategy.resolve("invalid.key");
} catch (LocatorException e) {
    logger.error("Locator not found: {}", e.getMessage());
}
```
