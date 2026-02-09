---
sidebar_position: 1
title: Guide for QA Engineers
---

# Guide for QA Engineers & Testers

This guide is tailored for Quality Assurance professionals who want to use TAFLEX for automated testing without deep Java programming knowledge.

## Who This Guide Is For

- 👤 QA Engineers transitioning to automation
- 👥 Manual testers learning automation
- 🧪 Test analysts creating test scenarios
- 📋 Quality coordinators managing test suites

## Your Role in TAFLEX

As a QA Engineer, you'll primarily work with:

| Area | Description |
|------|-------------|
| **📄 Locator Files** | Define and maintain element selectors in `.properties` files. No Java coding required! |
| **🧪 Test Data** | Create and manage test data in CSV, JSON, or database format. |
| **📝 Test Suites** | Organize tests into logical groups (smoke, regression, etc.). |
| **🐛 Defect Reporting** | Analyze test failures with automatic screenshots and logs. |

## Quick Start for QAs

### 1. Understanding the Project Structure

```
taflex/
├── src/
│   ├── test/
│   │   ├── resources/
│   │   │   ├── locators/     ← Your main workspace
│   │   │   │   ├── web/
│   │   │   │   │   └── *.properties
│   │   │   │   ├── api/
│   │   │   │   └── mobile/
│   │   │   └── data/         ← Test data files
│   └── java/                 ← Test code (managed by developers)
└── automation.properties     ← Configuration
```

### 2. Working with Locators

Locators are the **most important** part of your job in TAFLEX. They connect test code to application elements.

#### Locator File Format

```properties title="src/test/resources/locators/web/login.properties"
# Page URLs
login.page.url=https://app.example.com/login

# Login Form Elements
login.username.field=#username
login.password.field=#password
login.submit.button=button[type='submit']
login.forgot.password.link=a[href*='forgot-password']

# Error Messages
login.error.message=.error-banner
login.success.message=.success-toast
```

#### Finding Locators

Use browser DevTools to find element selectors:

**Chrome/Firefox:**
1. Right-click element → Inspect
2. Right-click in DevTools → Copy → Copy selector
3. Paste into .properties file

**Playwright Inspector:**
```bash
# Run with inspector
./gradlew webTest -Dplaywright.inspect=true
```

**Browser Extensions:**
- **Chrome**: SelectorGadget, ChroPath
- **Firefox**: Firebug, Try Xpath

#### Locator Best Practices

✅ **DO:**
- Use IDs when available: `#username`
- Use data attributes: `[data-testid='login-button']`
- Keep names descriptive: `checkout.shipping.address.field`

❌ **DON'T:**
- Use absolute XPath: `/html/body/div[3]/div[2]/input`
- Use auto-generated class names: `.sc-12dfef-3`
- Use text that changes often: `button:has-text("Submit Order")`

### 3. Managing Test Data

Create data files for different scenarios:

#### CSV Format (Recommended for tabular data)

```csv title="src/test/resources/data/users.csv"
username,password,role,expected_result
testuser1,Pass123!,admin,success
testuser2,WrongPass,user,failure
emptyuser,,user,validation_error
```

#### JSON Format (For complex data)

```json title="src/test/resources/data/order.json"
{
  "valid_order": {
    "product_id": "PROD-123",
    "quantity": 2,
    "shipping_address": {
      "street": "123 Main St",
      "city": "New York",
      "zip": "10001"
    }
  }
}
```

### 4. Running Tests

#### By Test Type

```bash
# Run all web tests
./gradlew webTest

# Run all API tests
./gradlew apiTest

# Run all mobile tests
./gradlew mobileTest
```

#### By Test Group

```bash
# Smoke tests (quick health check)
./gradlew smokeTest

# Regression tests (full suite)
./gradlew regressionTest
```

#### By Specific Test

```bash
# Run single test class
./gradlew test --tests LoginTests

# Run specific test method
./gradlew test --tests LoginTests.shouldLoginSuccessfully
```

### 5. Analyzing Test Results

#### TestNG Reports

After tests run, open:

```bash
# macOS
open build/reports/tests/index.html

# Linux
xdg-open build/reports/tests/index.html
```

#### Reading the Report

| Status | Meaning |
|--------|---------|
| 🟢 **Passed** | Test completed successfully, all assertions passed, no exceptions thrown |
| 🔴 **Failed** | Assertion failed, element not found, or timeout occurred |
| 🟡 **Skipped** | Test not executed, dependency failed, or configuration issue |

#### Screenshots on Failure

When a test fails, automatic screenshots are captured:

```
screenshots/
├── LoginTests_shouldLoginSuccessfully_1701234567890.png
├── CheckoutTests_shouldCompletePurchase_1701234567891.png
└── ...
```

**Naming convention**: `{TestClass}_{TestMethod}_{Timestamp}.png`

#### Log Files

Check detailed logs:

```bash
# View recent logs
tail -f logs/test-automation.log

# Search for specific test
grep "LoginTests" logs/test-automation.log
```

## Common QA Tasks

### Task 1: Adding a New Page

1. **Create locator file**:
   ```bash
   touch src/test/resources/locators/web/checkout.properties
   ```

2. **Add locators**:
   ```properties
   checkout.page.url=https://app.example.com/checkout
   checkout.shipping.address=#shipping-address
   checkout.payment.method=#payment-method
   checkout.place.order.button=#place-order
   ```

3. **Request test development** from developers

### Task 2: Updating Locators After UI Changes

When the application UI changes:

1. **Identify changed elements** using DevTools
2. **Update .properties file** with new selectors
3. **Run smoke tests** to verify: `./gradlew smokeTest`
4. **Update test documentation**

### Task 3: Creating Test Data for New Scenarios

1. **Identify required data fields**
2. **Create CSV or JSON file** in `src/test/resources/data/`
3. **Document data usage** in test case management tool
4. **Coordinate with developers** to use the data

### Task 4: Reporting Bugs

When a test reveals a bug:

1. **Capture screenshot** from `screenshots/` folder
2. **Copy relevant log entries** from `logs/test-automation.log`
3. **Document steps to reproduce**:
   - Test name
   - Execution mode (web/api/mobile)
   - Configuration used
   - Expected vs actual result
4. **Create bug ticket** with all evidence

## QA Checklist

### Before Test Execution

- [ ] Configuration file (`automation.properties`) is correct
- [ ] Target environment is accessible
- [ ] Test data is available and valid
- [ ] Locators are up-to-date

### During Test Execution

- [ ] Monitor test progress
- [ ] Note any unexpected behavior
- [ ] Check resource utilization

### After Test Execution

- [ ] Review all failed tests
- [ ] Verify screenshots for failures
- [ ] Check logs for errors
- [ ] Document results
- [ ] Report bugs
- [ ] Update test status in test management tool

## Best Practices for QAs

### 1. Version Control for Locators

```bash
# Create feature branch
git checkout -b feature/new-checkout-flow

# Modify locators
# ... edit files ...

# Commit changes
git add src/test/resources/locators/
git commit -m "Update locators for new checkout flow"

# Push and create PR
git push origin feature/new-checkout-flow
```

### 2. Naming Conventions

**Locator Keys:**
```properties
# Format: {page}.{element}.{type}
login.username.field       # ✓ Good
login.username             # ✗ Missing type
usr                        # ✗ Not descriptive
```

**Test Data Files:**
```
data/
├── users.csv              # User credentials
├── products.csv           # Product catalog
├── orders.json            # Order scenarios
└── environments/          # Environment-specific
    ├── staging-users.csv
    └── prod-users.csv
```

## Resources

- 📖 [TestNG Documentation](https://testng.org/doc/)
- 🌐 [CSS Selectors Guide](https://www.w3schools.com/cssref/css_selectors.php)
- 🐛 [Bug Reporting Best Practices](https://www.testlio.com/blog/the-ideal-bug-report/)

---

**Remember**: In TAFLEX, your role as a QA is focused on **what** to test (locators, data, scenarios) while developers handle **how** to test (code implementation). This separation allows you to contribute effectively without deep programming knowledge!
