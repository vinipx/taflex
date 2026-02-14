package io.github.vinipx.taflex.tests.web;

import io.github.vinipx.taflex.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Web dashboard tests using Playwright driver.
 */
public class DashboardTests extends BaseTest {
    
    @Test(groups = {"smoke", "regression"}, description = "Verify dashboard header is displayed")
    public void shouldDisplayDashboardHeader() {
        // Navigate to dashboard (assuming logged in state)
        driver.navigateTo("dashboard.page.url");
        
        // Verify dashboard header
        Assert.assertTrue(driver.isVisible("dashboard.header"),
            "Dashboard header should be visible");
    }
    
    @Test(groups = {"regression"}, description = "Verify user menu is accessible")
    public void shouldAccessUserMenu() {
        // Navigate to dashboard
        driver.navigateTo("dashboard.page.url");
        
        // Click user menu
        driver.click("dashboard.user.menu");
        
        // Verify menu options are visible
        Assert.assertTrue(driver.isVisible("dashboard.logout.button"),
            "Logout button should be visible in user menu");
    }
    
    @Test(groups = {"regression"}, description = "Verify notifications badge is present")
    public void shouldDisplayNotificationsBadge() {
        // Navigate to dashboard
        driver.navigateTo("dashboard.page.url");
        
        // Verify notifications badge exists
        Assert.assertTrue(driver.isVisible("dashboard.notifications.badge"),
            "Notifications badge should be visible");
    }
}