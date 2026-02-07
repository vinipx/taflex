package com.enterprise.taflex.tests.web;

import com.enterprise.taflex.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Web login tests using Playwright driver.
 */
public class LoginTests extends BaseTest {
    
    @Test(groups = {"smoke", "regression"}, description = "Verify successful login with valid credentials")
    public void shouldLoginSuccessfully() {
        // Navigate to login page
        driver.navigateTo("login.page.url");
        
        // Enter credentials
        driver.type("login.username.field", "testuser");
        driver.type("login.password.field", "password123");
        
        // Click login button
        driver.click("login.submit.button");
        
        // Verify successful login (dashboard visible)
        Assert.assertTrue(driver.isVisible("dashboard.welcome.message"),
            "Welcome message should be visible after successful login");
    }
    
    @Test(groups = {"regression"}, description = "Verify login fails with invalid credentials")
    public void shouldFailLoginWithInvalidCredentials() {
        // Navigate to login page
        driver.navigateTo("login.page.url");
        
        // Enter invalid credentials
        driver.type("login.username.field", "invaliduser");
        driver.type("login.password.field", "wrongpassword");
        
        // Click login button
        driver.click("login.submit.button");
        
        // Verify error message is displayed
        Assert.assertTrue(driver.isVisible("login.error.message"),
            "Error message should be visible for invalid credentials");
    }
    
    @Test(groups = {"regression"}, description = "Verify forgot password link is present")
    public void shouldDisplayForgotPasswordLink() {
        // Navigate to login page
        driver.navigateTo("login.page.url");
        
        // Verify forgot password link is visible
        Assert.assertTrue(driver.isVisible("login.forgot.password.link"),
            "Forgot password link should be visible");
    }
}