---
name: java-test-dev
description: Guides Java/TestNG test implementation in this framework. Use when adding or refactoring tests, fixtures, or assertions.
---
# Java Test Development

## Quick start
- Tests extend `BaseTest`; do not duplicate setup/teardown.
- Use `@Test` with `groups` and `description`.
- Keep payloads and datasets externalized; avoid hardcoded data.

## Implementation checklist
- [ ] Class names end with `Tests`
- [ ] Assertions are clear and message-rich
- [ ] Preconditions created in test or fixture
- [ ] No ordering dependency between tests

## Assertion guidance
- Use `Assert` for simple checks
- Use AssertJ for collections and object graphs

## Example
```java
@Test(groups = {"smoke", "api"}, description = "Verify GET orders endpoint returns 200")
public void shouldGetOrdersSuccessfully() {
    ApiDriverStrategy api = (ApiDriverStrategy) driver;
    ApiResponse response = api.get("orders.endpoint");
    Assert.assertEquals(response.getStatusCode(), 200, "GET /orders should return 200");
}
```
