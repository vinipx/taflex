# BDD Testing Tutorial

In this tutorial, you will learn how to create and run a Behavior-Driven Development (BDD) test using **Cucumber** and Gherkin syntax in TAFLEX.

---

## 1. Create a Feature File

Features are defined in `.feature` files using plain English. Create a file at `src/test/resources/features/google_search.feature`:

```gherkin
Feature: Google Search

  Scenario: Searching for TAFLEX Framework
    Given I navigate to "https://www.google.com"
    When I search for "TAFLEX Framework"
    Then I should see results related to "TAFLEX"
```

---

## 2. Implement Step Definitions

Step definitions bridge the Gherkin steps to actual Java code. Create `src/test/java/io/github/vinipx/taflex/steps/GoogleSteps.java`:

```java
package io.github.vinipx.taflex.steps;

import io.cucumber.java.en.*;
import io.github.vinipx.taflex.core.drivers.AutomationDriver;
import io.github.vinipx.taflex.core.drivers.DriverFactory;
import org.testng.Assert;

public class GoogleSteps {
    private AutomationDriver driver = DriverFactory.getDriver();

    @Given("I navigate to {string}")
    public void navigateTo(String url) {
        // You can use a raw URL or a locator key
        driver.getNativeDriver().navigate(url);
    }

    @When("I search for {string}")
    public void searchFor(String term) {
        driver.type("search.input.field", term);
        driver.click("search.submit.button");
    }

    @Then("I should see results related to {string}")
    public void verifyResults(String expected) {
        String content = driver.getText("search.results.container");
        Assert.assertTrue(content.contains(expected));
    }
}
```

---

## 3. Run the Test

Execute the BDD tests using the Gradle test task with Cucumber options:

```bash
./gradlew test -Dcucumber.options="src/test/resources/features"
```

---

## Key Benefits of this Approach

1. **Shared State**: The `AutomationDriver` is easily accessible via the `DriverFactory` singleton.
2. **Locator Management**: Use your `.properties` files within steps to keep selectors out of your Java code.
3. **Professional Reporting**: Cucumber integration with TestNG allows for beautiful HTML reports and integration with tools like ReportPortal.
