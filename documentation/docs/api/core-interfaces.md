# Core API Reference

This page documents the primary interfaces and classes provided by TAFLEX JS.

## AutomationDriver (Abstract Class)

The base class for all automation strategies.

| Method | Description |
|--------|-------------|
| `initialize(config)` | Initializes the driver session. |
| `terminate()` | Closes the driver session. |
| `navigateTo(url)` | Navigates to the specified URL or endpoint. |
| `findElement(logicalName)` | Resolves a locator and returns a wrapped element. |
| `loadLocators(pageName)` | Loads page-specific locators from JSON. |

## Element (Wrappers)

TAFLEX JS wraps native engine elements (Playwright or WDIO) to provide a consistent API.

### Common Methods

| Method | Description |
|--------|-------------|
| `click()` | Performs a click action. |
| `fill(value)` | Fills an input field with the specified value. |
| `getText()` | Returns the inner text of the element. |
| `isVisible()` | Returns `true` if the element is visible. |
| `isEnabled()` | Returns `true` if the element is enabled. |
| `waitFor(options)` | Waits for the element state (visible, hidden, etc). |

## LocatorManager

The engine behind hierarchical locator resolution.

| Method | Description |
|--------|-------------|
| `load(pageName)` | Loads and merges JSON locator files. |
| `resolve(logicalName)`| Returns the selector associated with the logical name. |
