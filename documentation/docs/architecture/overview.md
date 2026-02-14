---
sidebar_position: 1
title: Architecture Overview
---

# Architecture Overview

TAFLEX JS is built on a robust, extensible architecture that follows enterprise-grade design patterns. This document explains the architectural decisions and how different components interact.

## Design Philosophy

TAFLEX JS follows these core principles:

| Principle | Description |
|-----------|-------------|
| **🧩 Strategy Pattern** | Runtime driver resolution allows the same test code to run on Web, API, or Mobile without modification. |
| **📄 Separation of Concerns** | Test logic is completely decoupled from driver implementation and locator definitions. |
| **⚙️ Configuration Over Code** | Behavior is controlled through external configuration, not hardcoded values. |
| **🧪 Fast Feedback Loop** | High-performance execution using Playwright and Vitest for rapid development. |

## High-Level Architecture

```mermaid
flowchart TB
    subgraph "Test Layer"
        TC[Test Specs]
        BDD[BDD Features]
        FIX[Fixtures]
    end

    subgraph "Framework Core"
        DF[DriverFactory]
        LM[LocatorManager]
        CM[ConfigManager]
        DB[DatabaseManager]
    end

    subgraph "Driver Strategies"
        direction TB
        ADS[AutomationDriver<br/>Abstract Class]
        PDS[PlaywrightStrategy]
        APIS[PlaywrightApiStrategy]
        AXS[AxiosApiStrategy]
        MDS[WebdriverioMobileStrategy]
    end

    subgraph "Element Wrappers"
        PE[PlaywrightElement]
        ME[MobileElement]
    end

    subgraph "External Resources"
        JSON[JSON Locators]
        DATA[Test Data]
        ALR[Allure Reports]
    end

    TC --> FIX
    BDD --> FIX
    FIX --> DF
    FIX --> CM

    DF --> ADS
    ADS --> PDS
    ADS --> APIS
    ADS --> AXS
    ADS --> MDS

    PDS --> PE
    MDS --> ME

    ADS --> LM
    LM --> JSON

    TC --> DB
    DB --> DATA
```

## Component Breakdown

### 1. Driver Layer

The Driver Layer implements the **Strategy Pattern**, allowing runtime selection of the appropriate driver implementation.

```mermaid
classDiagram
    class AutomationDriver {
        <<abstract>>
        +initialize()
        +terminate()
        +navigateTo(String)
        +findElement(String)
        +loadLocators(String)
    }

    class PlaywrightStrategy {
        -browser
        -context
        -page
        +initialize()
        +navigateTo(String)
    }

    class PlaywrightApiStrategy {
        -requestContext
        +get(String)
        +post(String, Object)
    }

    class WebdriverioMobileStrategy {
        -client
        +initialize()
    }

    class AxiosApiStrategy {
        -client
        +get(String)
        +post(String, Object)
    }

    AutomationDriver <|-- PlaywrightStrategy
    AutomationDriver <|-- PlaywrightApiStrategy
    AutomationDriver <|-- AxiosApiStrategy
    AutomationDriver <|-- WebdriverioMobileStrategy
```

**Key Benefits:**
- ✅ Single test codebase for all platforms.
- ✅ Driver changes (e.g., swapping engines) don't affect test specs.
- ✅ Supports parallel execution with different strategies.

### 2. Locator System

All locators are externalized in JSON files using the **LocatorManager**.

```mermaid
sequenceDiagram
    participant Test as Test Spec
    participant LM as LocatorManager
    participant File as JSON Files

    Test->>LM: load("login")
    LM->>File: Read global.json
    File-->>LM: Global locators
    LM->>File: Read web/common.json
    File-->>LM: Web locators
    LM->>File: Read web/login.json
    File-->>LM: Page locators
    LM-->>Test: Merged Locator Cache Ready

    Test->>Test: findElement("username_field")
    Test->>LM: resolve("username_field")
    LM-->>Test: "#login-user"
```

**Locator Loading Order:**

1. `global.json` - Common across all modes.
2. `{mode}/common.json` - Mode-specific common locators.
3. `{mode}/{page}.json` - Page/feature-specific locators.

### 3. Configuration Management

The **ConfigManager** provides centralized access to validated environment variables:

```java
import { configManager } from './config/config.manager.js';

// Type-safe access with Zod validation
const browser = configManager.get('BROWSER');
const timeout = configManager.get('TIMEOUT');
```

### 4. Test Execution Flow

```mermaid
sequenceDiagram
    participant Suite as Test Runner
    participant FIX as Fixture
    participant Driver as Strategy
    participant Test as Spec

    Suite->>FIX: Setup Context
    FIX->>Driver: DriverFactory.create()
    Driver->>Driver: initialize(config)
    Driver-->>FIX: AutomationDriver

    FIX->>Test: Execute test(driver)
    Test->>Driver: navigateTo(), findElement()
    Driver->>Driver: Execute actions

    FIX->>Driver: terminate()
    Suite->>Suite: Finalize Reports
```

## Technology Stack

| Category | Technologies |
|----------|-------------|
| **Core Framework** | Node.js (ESM), Zod, Dotenv |
| **Web Testing** | Playwright, Chromium/Firefox/WebKit |
| **BDD Testing** | Gherkin, playwright-bdd |
| **API Testing** | Playwright (Hybrid) · Axios (Specialized) |
| **Mobile Testing** | WebdriverIO, Appium |
| **Unit Testing** | Vitest |
| **Database** | pg (Postgres), mysql2 (MySQL) |
| **Reporting** | Allure, Playwright HTML |

## Extensibility Points

TAFLEX JS is designed for extension at multiple levels:

### 1. Custom Driver Strategies
Simply extend the `AutomationDriver` base class and register it in the `DriverFactory`.

### 2. Custom Element Wrappers
Extend or create new element wrappers to support unique platform interactions while maintaining a consistent API.

### 3. Fixtures
Customize Playwright fixtures in `tests/fixtures.js` to inject global setup/teardown logic or custom dependencies.
