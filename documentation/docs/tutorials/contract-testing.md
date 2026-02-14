# Contract Testing Tutorial

This tutorial will walk you through creating your first Consumer-Driven Contract test using **Pact** in TAFLEX.

We will simulate a scenario where a **Web App** (Consumer) expects a specific response from a **User Service** (Provider).

---

## 1. Prerequisites

1.  Enable Pact in your `automation.properties`:
    ```properties
    pact.enabled=true
    ```

---

## 2. Create the Consumer Test

In Pact, the Consumer defines the interaction. Create a test class in `src/test/java/io/github/vinipx/taflex/tests/contract/UserServiceContractTest.java`:

```java
package io.github.vinipx.taflex.tests.contract;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.github.vinipx.taflex.core.contracts.PactManager;
import org.testng.annotations.Test;

public class UserServiceContractTest {
    private PactManager pactManager = new PactManager();

    @Pact(consumer = "user-web-app", provider = "user-service")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
            .given("user exists")
            .uponReceiving("a request for user 1")
                .path("/users/1")
                .method("GET")
            .willRespondWith()
                .status(200)
                .body("{\"id\": 1, \"name\": \"John Doe\"}")
            .toPact();
    }

    @Test
    public void testContract() {
        // Test logic using the Pact mock server
    }
}
```

---

## 3. Run the Test and Generate the Pact

Execute the test via Gradle:

```bash
./gradlew test --tests UserServiceContractTest
```

**What happens?**
- Pact starts a local mock server.
- The test interacts with the mock server.
- Upon success, a JSON "Pact file" is generated in `build/pacts/`.

---

## 4. Provider Verification

The Provider (API team) then takes this JSON file and verifies it against their real service implementation.

1.  Share the Pact file (manually or via a **Pact Broker**).
2.  The Provider runs their verification suite to ensure they haven't broken the contract.

For more details, refer to the [Pact Testing Guide](../guides/pact-testing).
