# API Testing Tutorial

Learn how to master API automation in TAFLEX using the specialized **ApiDriverStrategy** based on Apache HttpClient.

---

## 1. Creating Your First API Test

API tests in TAFLEX are built using TestNG and extend `BaseTest`. The framework handles configuration and driver lifecycle automatically.

### Step 1: Define Your Endpoint
Add your endpoint to `src/test/resources/locators/api/endpoints.properties`:

```properties
users.get.all=/users
user.details=/users/%s
```

### Step 2: Create the Test Class
Create a new file `src/test/java/io/github/vinipx/taflex/tests/api/UserApiTests.java`:

```java
package io.github.vinipx.taflex.tests.api;

import io.github.vinipx.taflex.base.BaseTest;
import io.github.vinipx.taflex.core.drivers.strategies.ApiDriverStrategy;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserApiTests extends BaseTest {

    @Test(groups = {"smoke"})
    public void shouldFetchUserDetails() {
        // 1. Cast the unified driver to API Strategy
        ApiDriverStrategy apiDriver = (ApiDriverStrategy) driver;

        // 2. Execute the request using a locator
        ApiDriverStrategy.ApiResponse response = apiDriver.get("users.get.all");

        // 3. Assert the results
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNotNull(response.getBody());
    }
}
```

---

## 2. Handling Payloads (POST/PUT)

For methods that require a body, you can pass a JSON string directly.

```java
@Test
public void shouldCreateUser() {
    ApiDriverStrategy apiDriver = (ApiDriverStrategy) driver;

    String payload = "{\"name\": \"John Doe\", \"job\": \"Engineer\"}";
    
    ApiDriverStrategy.ApiResponse response = apiDriver.post("user.create.endpoint", payload);
    
    Assert.assertEquals(response.getStatusCode(), 201);
}
```

---

## 3. How to Run API Tests

You can run your API tests using the dedicated Gradle task:

```bash
./gradlew apiTest
```

Or via TestNG groups:

```bash
./gradlew test -Dgroups=api
```

---

## 4. Best Practices

- **Externalize Everything**: Store all endpoints in `.properties` files.
- **Data Driven**: Use TestNG DataProviders for testing the same endpoint with different payloads.
- **Environment Aware**: Use `ConfigManager.getApiBaseUrl()` if you need to construct URLs dynamically, though the driver handles this automatically when using locators.
