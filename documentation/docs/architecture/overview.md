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
| **🧪 Modern Java** | Leverages Java 21 features for clean and efficient automation code. |

## High-Level Architecture

```mermaid
flowchart TB
    subgraph "Test Layer"
        TC[TestNG Tests]
        BASE[BaseTest]
        LIST[Listeners]
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
        PE[PlaywrightElement]
        AE[ApiElement]
        ME[MobileElement]
    end

    subgraph "External Resources"
        PROP[Properties Locators]
        DATA[Test Data]
        RPT[ReportPortal / Xray]
    end

    TC --> BASE
    BASE --> DF
    BASE --> CM

    DF --> ADS
    ADS --> PDS
    ADS --> APIS
    ADS --> MDS

    PDS --> PE
    APIS --> AE
    MDS --> ME

    ADS --> LF
    LF --> PROP

    TC --> DB
    DB --> DATA
    TC --> RPT
```

## Component Breakdown

### 1. Driver Layer

The Driver Layer implements the **Strategy Pattern**, allowing runtime selection of the appropriate driver implementation based on the `execution.mode` property.

```mermaid
classDiagram
    class AutomationDriver {
        <<interface>>
        +initialize()
        +terminate()
        +navigateTo(String)
        +click(String)
        +type(String, String)
        +isVisible(String)
    }

    class PlaywrightDriverStrategy {
        -browser
        -page
        +initialize()
    }

    class ApiDriverStrategy {
        -httpClient
        +get(String)
        +post(String, String)
    }

    class MobileDriverStrategy {
        -appiumDriver
        +initialize()
    }

    AutomationDriver <|.. PlaywrightDriverStrategy
    AutomationDriver <|.. ApiDriverStrategy
    AutomationDriver <|.. MobileDriverStrategy
```

### 2. Locator System

All locators are externalized in `.properties` files and resolved by the `LocatorFactory`.

**Locator Loading Order:**

1. `global.properties` - Common across all modes.
2. `{mode}/common.properties` - Mode-specific common locators.
3. `{mode}/{page}.properties` - Page/feature-specific locators.

### 3. Configuration Management

The **ConfigManager** provides centralized access to properties defined in `automation.properties`, with support for environment variable overrides.

### 4. Test Execution Flow

1. **Suite Start**: TestNG starts the execution.
2. **Setup**: `BaseTest` calls `DriverFactory` to get the correct driver instance.
3. **Initialization**: The driver initializes the underlying tool (Playwright, HttpClient, or Appium).
4. **Execution**: Tests interact with the `AutomationDriver` using logical locator names.
5. **Teardown**: `BaseTest` terminates the driver and reports results.

## Technology Stack

| Category | Technologies |
|----------|-------------|
| **Core Language** | Java 21+ |
| **Build Tool** | Gradle 8.5 |
| **Test Runner** | TestNG |
| **Web Testing** | Playwright for Java |
| **API Testing** | Apache HttpClient |
| **Mobile Testing** | Appium (Java Client) |
| **Database** | HikariCP, JDBC |
| **Reporting** | ReportPortal, Xray Cloud |
| **Logging** | SLF4J with Logback |
