---
sidebar_position: 1
title: Architecture Overview
---

# Architecture Overview

TAFLEX is built on a robust, extensible architecture that follows enterprise-grade design patterns. This document explains the architectural decisions and how different components interact.

## Design Philosophy

TAFLEX follows these core principles:

| Principle | Description |
|-----------|-------------|
| **🧩 Strategy Pattern** | Runtime driver resolution allows the same test code to run on Web, API, or Mobile without modification. |
| **📄 Separation of Concerns** | Test logic is completely decoupled from driver implementation and locator definitions. |
| **⚙️ Configuration Over Code** | Behavior is controlled through external configuration, not hardcoded values. |
| **🧪 Test-First Design** | Every component is designed with testability in mind, following TDD principles. |

## High-Level Architecture

```mermaid
flowchart TB
    subgraph "Test Layer"
        TC[Test Classes]
        BT[BaseTest]
        TL[TestListener]
    end

    subgraph "Framework Core"
        DF[DriverFactory]
        LF[LocatorFactory]
        CM[ConfigManager]
        DB[DatabaseManager]
    end

    subgraph "Driver Strategies"
        direction TB
        ADS[AutomationDriver<br/>Interface]
        PDS[PlaywrightDriverStrategy]
        APIS[ApiDriverStrategy]
        MDS[MobileDriverStrategy]
    end

    subgraph "Element Wrappers"
        E[Element<br/>Interface]
        PE[PlaywrightElement]
        AE[ApiElement]
        ME[MobileElement]
    end

    subgraph "Locator System"
        LS[LocatorStrategy<br/>Interface]
        PLS[PropertiesLocatorStrategy]
    end

    subgraph "External Resources"
        PROP[.properties Files]
        DATA[Test Data]
        RP[ReportPortal]
    end

    TC --> BT
    BT --> DF
    BT --> CM
    TC --> TL

    DF --> ADS
    ADS --> PDS
    ADS --> APIS
    ADS --> MDS

    PDS --> PE
    APIS --> AE
    MDS --> ME

    TC --> LF
    LF --> LS
    LS --> PLS
    PLS --> PROP

    TC --> DB
    DB --> DATA

    TL --> RP
```

## Component Breakdown

### 1. Driver Layer

The Driver Layer implements the **Strategy Pattern**, allowing runtime selection of the appropriate driver implementation.

```mermaid
classDiagram
    class AutomationDriver {
        <<interface>>
        +initialize()
        +terminate()
        +navigateTo(String)
        +click(String)
        +type(String, String)
        +findElement(String) Element
    }

    class PlaywrightDriverStrategy {
        -Playwright playwright
        -Browser browser
        -Page page
        +initialize()
        +navigateTo(String)
    }

    class ApiDriverStrategy {
        -CloseableHttpClient httpClient
        +get(String) ApiResponse
        +post(String, String) ApiResponse
    }

    class MobileDriverStrategy {
        -AppiumDriver driver
        +swipe(int, int, int, int)
    }

    AutomationDriver <|.. PlaywrightDriverStrategy
    AutomationDriver <|.. ApiDriverStrategy
    AutomationDriver <|.. MobileDriverStrategy
```

**Key Benefits:**
- ✅ Single test codebase for all platforms
- ✅ Easy to add new driver types
- ✅ Driver changes don't affect test code
- ✅ Supports parallel execution with different drivers

### 2. Locator System

All locators are externalized in `.properties` files using the **PropertiesLocatorStrategy**.

```mermaid
sequenceDiagram
    participant Test as Test Code
    participant DF as DriverFactory
    participant PS as PropertiesLocatorStrategy
    participant File as .properties Files

    Test->>DF: getDriver()
    DF->>PS: loadAllLocators()
    PS->>File: Read global.properties
    File-->>PS: Global locators
    PS->>File: Read web/common.properties
    File-->>PS: Web locators
    PS->>File: Read page-specific.properties
    File-->>PS: Page locators
    PS-->>DF: Locator cache ready
    DF-->>Test: Driver initialized

    Test->>DF: click("login.button")
    DF->>PS: resolve("login.button")
    PS-->>DF: "#submit-btn"
    DF->>DF: Execute click on selector
```

**Locator Loading Order:**

1. `global.properties` - Common across all modes
2. `{mode}/common.properties` - Mode-specific common locators
3. `{mode}/*.properties` - Page/feature-specific locators

Later files override earlier ones, allowing fine-grained customization.

### 3. Configuration Management

The **ConfigManager** provides centralized access to `automation.properties`:

```java
// Get string property
String browser = ConfigManager.getProperty("web.browser");

// Get with default
int timeout = ConfigManager.getIntProperty("web.timeout", 30);

// Get boolean
boolean headless = ConfigManager.getBooleanProperty("web.headless", false);
```

**Configuration Hierarchy:**

1. System properties (highest priority)
2. `automation.properties` file
3. `automation.properties.template` (defaults)

### 4. Test Execution Flow

```mermaid
sequenceDiagram
    participant Suite as Test Suite
    participant BT as BaseTest
    participant Driver as Driver
    participant Test as Test Method
    participant Report as ReportPortal

    Suite->>BT: @BeforeSuite
    BT->>BT: Initialize configuration

    Suite->>BT: @BeforeMethod
    BT->>Driver: DriverFactory.getDriver()
    Driver->>Driver: initialize()
    Driver-->>BT: AutomationDriver

    BT->>Test: Execute test
    Test->>Driver: Navigate, Click, Type...
    Driver->>Driver: Execute actions

    alt Test Fails
        Test-->>BT: Exception
        BT->>Driver: captureScreenshot()
        BT->>Report: Log failure
    end

    BT->>BT: @AfterMethod
    BT->>Driver: terminate()

    Suite->>BT: @AfterSuite
    BT->>BT: Cleanup resources
```

## Data Flow

### Test Data Management

```mermaid
flowchart LR
    subgraph "Test"
        TC[Test Case]
    end

    subgraph "Data Sources"
        CSV[CSV Files]
        JSON[JSON Files]
        DB[Database]
        PROP[Properties]
    end

    subgraph "Providers"
        TDP[TestDataProvider<br/>Interface]
        CP[CsvDataProvider]
        JP[JsonDataProvider]
        DP[DatabaseProvider]
    end

    TC --> TDP
    TDP --> CP
    TDP --> JP
    TDP --> DP
    CP --> CSV
    JP --> JSON
    DP --> DB
    CP --> PROP
```

## Parallel Execution Architecture

TAFLEX supports parallel execution at multiple levels:

```mermaid
flowchart TB
    subgraph "Test Suite"
        direction TB
        T1[Test Thread 1]
        T2[Test Thread 2]
        T3[Test Thread 3]
        T4[Test Thread 4]
    end

    subgraph "Driver Instances"
        D1[Driver Instance 1]
        D2[Driver Instance 2]
        D3[Driver Instance 3]
        D4[Driver Instance 4]
    end

    subgraph "Resources"
        B1[Browser 1]
        B2[Browser 2]
        B3[Browser 3]
        B4[Browser 4]
    end

    T1 --> D1 --> B1
    T2 --> D2 --> B2
    T3 --> D3 --> B3
    T4 --> D4 --> B4
```

**Configuration:**

```properties
parallel.enabled=true
parallel.threads=4
```

## Reporting Integration

```mermaid
sequenceDiagram
    participant Test as TestNG Test
    participant TL as TestListener
    participant RP as ReportPortal

    Test->>TL: onTestStart()
    TL->>RP: Start test item

    Test->>TL: onTestSuccess() / onTestFailure()
    alt Success
        TL->>RP: Finish with PASSED
    else Failure
        TL->>RP: Capture screenshot
        TL->>RP: Attach stack trace
        TL->>RP: Finish with FAILED
    end

    TL->>RP: Log attachments
```

## Technology Stack

| Category | Technologies |
|----------|-------------|
| **Core Framework** | Java 21+, Gradle 8.5+, TestNG 7.8+ |
| **Web Testing** | Playwright 1.41+, Chromium/Firefox/WebKit |
| **API Testing** | Apache HttpClient 4.5+, Jackson 2.16+ |
| **Mobile Testing** | Appium 9.0+, Android/iOS |
| **Data & Reporting** | HikariCP 5.1+, ReportPortal 5.3+, SLF4J + Logback |
| **Utilities** | AssertJ 3.24+, Apache Commons |

## Extensibility Points

TAFLEX is designed for extension at multiple levels:

### 1. Custom Driver Strategies

```java
public class CustomDriverStrategy implements AutomationDriver {
    // Implement interface methods
    // Register in DriverFactory
}
```

### 2. Custom Locator Strategies

```java
public class DatabaseLocatorStrategy implements LocatorStrategy {
    // Load locators from database
    // Register in LocatorFactory
}
```

### 3. Custom Test Listeners

```java
public class CustomListener extends TestListenerAdapter {
    // Override lifecycle methods
    // Add to testng.xml
}
```

## Performance Considerations

### Driver Caching

The `DriverFactory` caches driver instances to avoid repeated initialization:

```java
// First call - creates and caches
AutomationDriver driver1 = DriverFactory.getDriver("web");

// Second call - returns cached instance
AutomationDriver driver2 = DriverFactory.getDriver("web");
// driver1 == driver2
```

### Locator Caching

Locators are loaded once and cached in memory:

```java
// Loads and caches all .properties files
LocatorStrategy strategy = LocatorFactory.getLocatorStrategy();

// Subsequent resolves use cache
String selector = strategy.resolve("login.button"); // Fast lookup
```

### Connection Pooling

Database connections use HikariCP for optimal performance:

```properties
db.pool.size=10
db.pool.minIdle=2
```

## Security

### Configuration Security

- `automation.properties` is gitignored
- Sensitive data never committed
- Template file provides safe defaults

### Credential Management

```properties
# Use environment variables or secure vaults
db.password=${DB_PASSWORD}
api.auth.token=${API_TOKEN}
```
