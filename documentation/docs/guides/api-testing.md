# API Testing

TAFLEX provides a specialized API driver based on **Apache HttpClient** for high-performance API automation.

## 1. Configuration

Configure your API base URL and timeouts in `automation.properties`:

```properties
api.base.url=https://api.staging.example.com
api.timeout=30
api.content.type=application/json
```

---

## 2. Writing an API Test

To write an API test, your test class should extend `BaseTest`. You can then cast the `driver` to `ApiDriverStrategy` to access API-specific methods.

```java
package io.github.vinipx.taflex.tests.api;

import io.github.vinipx.taflex.base.BaseTest;
import io.github.vinipx.taflex.core.drivers.strategies.ApiDriverStrategy;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserApiTests extends BaseTest {
    
    @Test(groups = {"smoke", "api"})
    public void shouldGetUsersSuccessfully() {
        // Cast driver to API strategy
        ApiDriverStrategy apiDriver = (ApiDriverStrategy) driver;
        
        // Execute GET request using externalized locator for the endpoint
        ApiDriverStrategy.ApiResponse response = apiDriver.get("users.endpoint");
        
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 200, "GET /users should return 200 OK");
        
        // Verify response body
        Assert.assertNotNull(response.getBody(), "Response body should not be null");
    }
}
```

---

## 3. Available Methods

The API driver supports standard HTTP methods:

- `apiDriver.get(locator)`
- `apiDriver.post(locator, payload)`
- `apiDriver.put(locator, payload)`
- `apiDriver.patch(locator, payload)`
- `apiDriver.delete(locator)`

### ApiResponse Object
Every API call returns an `ApiResponse` object with the following methods:
- `getStatusCode()`: Returns the HTTP status code.
- `getBody()`: Returns the response body as a String.
- `getContentType()`: Returns the response content type.

---

## 4. Endpoint Externalization

Endpoints should be stored in your locator properties files (e.g., `src/test/resources/locators/api/endpoints.properties`):

```properties
users.endpoint=/v1/users
user.create.endpoint=/v1/users/create
```
