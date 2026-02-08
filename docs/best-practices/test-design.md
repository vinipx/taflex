# Test Design Best Practices

These guidelines help keep tests reliable, readable, and fast across Web, API, and Mobile suites.

## Core principles
- **Keep tests orchestration-only**: put implementation details in driver strategies.
- **Prefer deterministic checks**: avoid sleeps and unstable timing assumptions.
- **Use explicit assertions**: make each assertion focused and readable.
- **Externalize data**: locators and test data belong in `src/test/resources`.

## Structure and naming
- **Class names** end with `Tests` and reflect the feature under test.
- **Test groups** use TestNG groups: `smoke`, `regression`, `web`, `api`, `mobile`.
- **Descriptions** in `@Test` explain the business intent.

## Data and preconditions
- **Create preconditions** inside the test or fixture (e.g., create before delete).
- **Avoid shared state** between tests to prevent ordering dependencies.
- **Use providers/resources** for datasets instead of inline hardcoding.

## Reliability tips
- **Avoid sleeps**: use strategy-level waits or framework waits.
- **Isolate side effects**: clean up any created data.
- **Limit scope**: one behavior per test.

## Example
```java
@Test(groups = {"smoke", "api"}, description = "Verify GET orders endpoint returns 200")
public void shouldGetOrdersSuccessfully() {
    ApiDriverStrategy api = (ApiDriverStrategy) driver;
    ApiResponse response = api.get("orders.endpoint");
    Assert.assertEquals(response.getStatusCode(), 200, "Orders endpoint should return 200");
}
```
