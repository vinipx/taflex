---
sidebar_position: 1
title: Quick Start Guide
---

# Quick Start Guide

Get up and running with TAFLEX in under 5 minutes.

## Prerequisites

Before you begin, ensure you have the following installed:

### Required

- **Java 21+** - [Download from Adoptium](https://adoptium.net/)
- **Gradle 8.5+** - Or use the included wrapper

### Optional (for specific testing)

- **Appium** - For mobile testing (`npm install -g appium`)
- **Docker** - For ReportPortal local setup
- **Node.js** - Required for Appium

## Step 1: Clone the Repository

```bash
git clone https://github.com/vinipx/taflex.git
cd taflex
```

## Step 2: Run Setup Script

The setup script will:
- Verify Java 21+ installation
- Check Gradle availability
- Create the configuration file
- Set up required directories
- Download dependencies
- Compile the project

```bash
./setup.sh
```

<details>
<summary><strong>✅ Expected Output</strong></summary>

```
==========================================
  TAFLEX - Framework Setup
==========================================

ℹ Detected OS: macos

Checking Java installation...
✓ Java 21.0.x found (>= 21)
ℹ Using: /Users/username/Library/Java/JavaVirtualMachines/corretto-23.0.2/Contents/Home/bin/java

Checking Gradle installation...
✓ Gradle wrapper found

Setting up configuration...
✓ Created automation.properties from template
! Please edit automation.properties with your environment settings

Creating required directories...
✓ Created: logs
✓ Created: screenshots

Resolving Gradle dependencies...
✓ Dependencies resolved successfully

Compiling test sources...
✓ Compilation successful

==========================================
  Setup Complete!
==========================================
```

</details>

## Step 3: Configure Your Environment

Edit the `automation.properties` file:

```bash
# On macOS/Linux
nano automation.properties

# On Windows
notepad automation.properties
```

At minimum, set these values:

```properties title="automation.properties"
# Execution mode: web | api | mobile
execution.mode=web

# Web Configuration
web.browser=chromium
web.base.url=https://your-app-url.com

# API Configuration (if running API tests)
api.base.url=https://your-api-url.com
```

:::tip Multiple Environments
You can maintain separate configuration files for different environments:
```bash
cp automation.properties automation.staging.properties
cp automation.properties automation.production.properties
```
:::

## Step 4: Run Your First Test

### Web Test

```bash
./gradlew webTest
```

### API Test

```bash
./gradlew apiTest
```

### Mobile Test

```bash
./gradlew mobileTest
```

## Step 5: View Results

After tests complete, check:

- **Test Reports**: `build/reports/tests/index.html`
- **Screenshots**: `screenshots/` (on failure)
- **Logs**: `logs/test-automation.log`

<details>
<summary><strong>Open Reports</strong></summary>

```bash
# macOS
open build/reports/tests/index.html

# Linux
xdg-open build/reports/tests/index.html

# Windows
start build/reports/tests/index.html
```

</details>

## Next Steps

- **Read the Guides** — Learn about [best practices](../best-practices/test-design.md) and explore the [architecture](../architecture/overview.md).
- **Explore Examples** — Check out sample tests in `src/test/java/io/github/vinipx/tests/`.
- **Understand Architecture** — Learn how TAFLEX works under the hood in the [Architecture section](../architecture/overview.md).

## Troubleshooting

<details>
<summary><strong>Java version not found</strong></summary>

Ensure Java 21+ is installed and in your PATH:

```bash
java -version
```

If not found, install from [Adoptium](https://adoptium.net/) and set JAVA_HOME:

```bash
export JAVA_HOME=/path/to/java21
export PATH=$JAVA_HOME/bin:$PATH
```
</details>

<details>
<summary><strong>Permission denied on setup.sh</strong></summary>

Make the script executable:

```bash
chmod +x setup.sh
```
</details>

<details>
<summary><strong>Gradle command not found</strong></summary>

Use the Gradle wrapper instead:

```bash
./gradlew webTest
```
</details>

## Quick Commands Reference

| Command | Description |
|---------|-------------|
| `./setup.sh` | Run initial setup |
| `./gradlew webTest` | Run all Web tests |
| `./gradlew apiTest` | Run all API tests |
| `./gradlew mobileTest` | Run all Mobile tests |
| `./gradlew smokeTest` | Run smoke tests |
| `./gradlew regressionTest` | Run regression tests |
| `./gradlew test --tests LoginTests` | Run specific test class |
| `./gradlew clean` | Clean build artifacts |
| `./gradlew build` | Full build with tests |

---

**You're all set!** 🎉 Start exploring TAFLEX and write your first test.
