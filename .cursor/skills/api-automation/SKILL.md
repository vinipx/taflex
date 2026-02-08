---
name: api-automation
description: Provides API automation guidance using ApiDriverStrategy and externalized endpoints. Use when adding or updating API tests or drivers.
---
# API Automation

## Quick start
- Use `ApiDriverStrategy` via `DriverFactory`.
- Resolve endpoints by key (e.g., `orders.endpoint`), not raw URLs.
- Validate status codes and response content.

## Workflow
1. Arrange: prepare payload/test data from resources.
2. Act: call `get/post/put/delete` on `ApiDriverStrategy`.
3. Assert: verify status and key response fields.

## Checklist
- [ ] Endpoint keys live in locator properties
- [ ] Base URL from `ConfigManager`
- [ ] Assertions include error messages

## Example
```java
ApiDriverStrategy api = (ApiDriverStrategy) driver;
ApiResponse response = api.post("order.create.endpoint", payload);
Assert.assertEquals(response.getStatusCode(), 201, "POST /orders should return 201");
```
