# Locators Management

TAFLEX uses a hierarchical system based on `.properties` files for managing locators, allowing for clean separation of concerns and easy maintenance.

## Directory Structure

Locators are organized in the `src/test/resources/locators/` directory:

```text
src/test/resources/locators/
├── global.properties          # Common across all modes
├── web/
│   ├── common.properties      # Web-specific common locators
│   └── login.properties       # Page-specific locators (Login)
├── api/
│   └── endpoints.properties   # API endpoints
└── mobile/
    └── selectors.properties   # Mobile selectors
```

---

## 🏗️ Hierarchical Loading

When a locator is requested, the framework searches through the files in a specific order. This allows you to override global locators with mode-specific or page-specific ones.

1. **Global**: `global.properties`
2. **Common (Mode)**: `[mode]/common.properties`
3. **Page**: `[mode]/[page].properties`

---

## 📄 Example: `web/login.properties`

```properties
# Login Page Elements
login.username.field=#username
login.password.field=#password
login.submit.button=button[type='submit']
login.error.message=.alert-danger
```

---

## 🧑‍💻 Usage in Tests

You don't need to specify the file name in your tests. The `AutomationDriver` handles the resolution automatically.

```java
@Test
public void loginTest() {
    driver.navigateTo("login.page.url");
    
    // The logical name is used, the framework finds the CSS selector
    driver.type("login.username.field", "myuser");
    driver.type("login.password.field", "mypassword");
    driver.click("login.submit.button");
    
    Assert.assertTrue(driver.isVisible("dashboard.welcome.message"));
}
```

The `driver` methods (like `type`, `click`, `isVisible`) all accept a logical locator name, which is resolved to the actual selector (CSS, XPath, etc.) at runtime.
