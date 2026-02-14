# Pact Contract Testing

Pact is a consumer-driven contract testing tool that ensures different services can communicate correctly.

## Why Contract Testing?

Traditional E2E tests are often slow and flaky. Contract testing allows you to:
- **Fast Feedback**: Catch breaking API changes at build time.
- **Reduce Flakiness**: Replace unstable E2E tests with fast, deterministic contract tests.

---

## 🛠 Configuration

Enable Pact in your `automation.properties` file:

```properties
# Enable Pact
pact.enabled=true
```

---

## 🧑‍💻 Consumer Testing

The `PactManager` helps manage the lifecycle of contract tests.

### Usage in Tests

```java
public class MyContractTest {
    private PactManager pactManager = new PactManager();

    @BeforeClass
    public void setup() {
        pactManager.setup("my-consumer", "my-provider");
    }

    @Test
    public void testContract() {
        if (pactManager.isEnabled()) {
            // Use Pact DSL to define expectations
        }
    }
}
```

### Dependencies
Pact dependencies are included in `build.gradle` under the `implementation` scope to ensure they are available for both the framework core and your tests.

```gradle
implementation "au.com.dius.pact:consumer:4.6.3"
```
