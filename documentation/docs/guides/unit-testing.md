# Unit Testing

TAFLEX uses **TestNG** and **Mockito** for unit testing its core components. This ensures that the framework's internal logic—such as configuration loading, locator resolution, and driver factory—is reliable and independently verified.

## Running Unit Tests

You can run the unit test suite using the dedicated source set via Gradle:

```bash
./gradlew test --tests io.github.vinipx.taflex.tests.unit.*
```

To run all tests (unit and integration):
```bash
./gradlew test
```

## Test Structure

Unit tests are located in `src/test/java/io/github/vinipx/taflex/tests/unit/`.

### ConfigManagerTest
Verifies that `ConfigManager` correctly loads properties from the template and overrides them with system properties. It also ensures the Singleton pattern is robust and that circular dependencies are avoided.

### LocatorFactoryTest
Ensures that the factory correctly instantiates and caches both `PropertiesLocatorStrategy` and `JsonLocatorStrategy` based on the requested type.

### DriverFactoryTest
Checks that the correct driver implementation (Playwright, API, or Mobile) is returned based on the `execution.mode` and that caching works as expected.

### CapabilityBuilderTest
Validates that Selenium/Appium capabilities are correctly constructed for different cloud platforms like **BrowserStack** and **SauceLabs** based on the properties.

## Mocking in Tests

We use **Mockito** to isolate components during testing. This allows us to mock external dependencies like the file system, network responses, or native driver behaviors to focus on testing the framework's logic.
