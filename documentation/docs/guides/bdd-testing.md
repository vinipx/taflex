# BDD Testing

TAFLEX supports Behavior-Driven Development (BDD) using **Cucumber** and Gherkin syntax. This allows you to write tests in a human-readable format that serves as living documentation.

## Project Structure

BDD files are typically located in the `src/test/resources/features/` directory and step definitions in `src/test/java/io/github/vinipx/taflex/steps/`.

## Writing a Feature File

Create a file ending in `.feature`:

```gherkin
Feature: User Login

  @smoke
  Scenario: Successful login
    Given I navigate to "login.page.url"
    When I enter "myuser" as username and "mypass" as password
    And I click on the login button
    Then I should see the dashboard welcome message
```

## Writing Step Definitions

Create a Java class for your step definitions. You can inject the `AutomationDriver` using the `DriverFactory`:

```java
package io.github.vinipx.taflex.steps;

import io.cucumber.java.en.*;
import io.github.vinipx.taflex.core.drivers.AutomationDriver;
import io.github.vinipx.taflex.core.drivers.DriverFactory;
import org.testng.Assert;

public class LoginSteps {
    private AutomationDriver driver = DriverFactory.getDriver();

    @Given("I navigate to {string}")
    public void navigateTo(String urlKey) {
        driver.navigateTo(urlKey);
    }

    @When("I enter {string} as username and {string} as password")
    public void enterCredentials(String user, String pass) {
        driver.type("login.username.field", user);
        driver.type("login.password.field", pass);
    }

    @When("I click on the login button")
    public void clickLogin() {
        driver.click("login.submit.button");
    }

    @Then("I should see the dashboard welcome message")
    public void verifyDashboard() {
        Assert.assertTrue(driver.isVisible("dashboard.welcome.message"));
    }
}
```

## Running BDD Tests

The framework is configured to run Cucumber tests via TestNG. You can run them using the Gradle test task:

```bash
./gradlew test -Dcucumber.options="src/test/resources/features"
```

## How it works

TAFLEX integrates Cucumber with TestNG. Step definitions have full access to the unified `AutomationDriver`, allowing you to use externalized locators and platform-agnostic interactions exactly like in standard scripted tests.
