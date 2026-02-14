package io.github.vinipx.taflex.taflex.tests.mobile;

import io.github.vinipx.taflex.taflex.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Mobile login tests using Appium driver.
 */
public class MobileLoginTests extends BaseTest {
    
    @Test(groups = {"smoke", "regression"}, description = "Verify successful mobile login")
    public void shouldLoginSuccessfullyOnMobile() {
        // Enter username
        driver.type("mobile.login.username.field", "testuser");
        
        // Enter password
        driver.type("mobile.login.password.field", "password123");
        
        // Click login button
        driver.click("mobile.login.submit.button");
        
        // Wait for dashboard
        driver.waitForVisible("mobile.dashboard.welcome.text", 10);
        
        // Verify welcome message
        Assert.assertTrue(driver.isVisible("mobile.dashboard.welcome.text"),
            "Welcome text should be visible after login");
    }
    
    @Test(groups = {"regression"}, description = "Verify forgot password link on mobile")
    public void shouldDisplayForgotPasswordLinkOnMobile() {
        // Verify forgot password link is visible
        Assert.assertTrue(driver.isVisible("mobile.login.forgot.password"),
            "Forgot password link should be visible on login screen");
    }
    
    @Test(groups = {"regression"}, description = "Verify login with empty credentials shows error")
    public void shouldShowErrorForEmptyCredentials() {
        // Click login without entering credentials
        driver.click("mobile.login.submit.button");
        
        // Verify error message (assuming there's an error message element)
        // This test assumes your app shows a validation error
        // Adjust locator as needed for your app
        sleep(1000); // Wait for error animation
        
        // Example assertion - customize for your app
        // Assert.assertTrue(driver.isVisible("mobile.login.error.message"),
        //     "Error message should be displayed for empty credentials");
    }
}