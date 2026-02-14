---
sidebar_position: 1
title: Quick Start
---

# Quick Start Guide

Get up and running with TAFLEX in under 5 minutes.

## 1. Prerequisites

TAFLEX requires:
- **Java 21** or higher.
- **Gradle 8.5** or higher (or use the included wrapper).
- **Git** for version control.

## 2. Installation

```bash
# Clone the repository
git clone https://github.com/vinipx/taflex.git
cd taflex

# Run the automated setup
./setup.sh
```

The `setup.sh` script will:
- Check your Java version (requires 21+).
- Verify Gradle installation.
- Create `automation.properties` from the template.
- Resolve dependencies and compile the project.

## 3. Configuration

Edit the `automation.properties` file in the root directory:

```properties
# Execution Mode: web | api | mobile
execution.mode=web

# Web Settings
web.browser=chromium
web.headless=true
web.base.url=https://www.google.com

# API Settings
api.base.url=https://jsonplaceholder.typicode.com

# Reporting configuration
reportportal.enabled=false
xray.enabled=false
```

## 4. Running Your First Test

### Run by Category
TAFLEX provides Gradle tasks for different test types:

```bash
# Run Web tests
./gradlew webTest

# Run API tests
./gradlew apiTest

# Run Mobile tests
./gradlew mobileTest
```

### Run by Groups
You can also run tests using TestNG groups:

```bash
# Run smoke tests
./gradlew test -Dgroups=smoke

# Run regression tests
./gradlew test -Dgroups=regression
```

## 5. Visualizing Results

### Local Reports
- **TestNG HTML Report**: Located at `build/reports/tests/index.html`.
- **Screenshots**: Automatically captured on failure in the `screenshots/` directory.
- **Logs**: Check `logs/test-automation.log` for detailed execution logs.

### Enterprise Reporting
To use **ReportPortal** or **Xray**, configure the respective properties in `automation.properties`.

---

## 🏗️ What's Next?

- [Architecture Overview](../architecture/overview.md)
- [How to manage Locators](../guides/locators.md)
- [API Testing Guide](../guides/api-testing.md)
