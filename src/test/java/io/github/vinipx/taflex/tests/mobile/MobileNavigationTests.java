package io.github.vinipx.taflex.tests.mobile;

import io.github.vinipx.taflex.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Mobile navigation tests using Appium driver.
 */
public class MobileNavigationTests extends BaseTest {
    
    private static final String REGRESSION = "regression";

    @Test(groups = {"smoke", REGRESSION}, description = "Verify bottom navigation is present")
    public void shouldDisplayBottomNavigation() {
        // Navigate to home
        driver.click("mobile.nav.home");
        
        // Verify navigation items are visible
        Assert.assertTrue(driver.isVisible("mobile.nav.profile"),
            "Profile navigation should be visible");
        Assert.assertTrue(driver.isVisible("mobile.nav.settings"),
            "Settings navigation should be visible");
    }
    
    @Test(groups = {REGRESSION}, description = "Verify navigation to profile screen")
    public void shouldNavigateToProfileScreen() {
        // Click profile navigation
        driver.click("mobile.nav.profile");
        
        // Verify profile icon is visible on profile screen
        Assert.assertTrue(driver.isVisible("mobile.dashboard.profile.icon"),
            "Profile icon should be visible on profile screen");
    }
    
    @Test(groups = {REGRESSION}, description = "Verify hamburger menu opens navigation drawer")
    public void shouldOpenNavigationDrawer() {
        // Click menu button
        driver.click("mobile.dashboard.menu.button");
        
        // Wait for drawer to open
        sleep(500);
        
        // Verify logout button is visible in drawer
        Assert.assertTrue(driver.isVisible("mobile.dashboard.logout.button"),
            "Logout button should be visible in navigation drawer");
    }
    
    @Test(groups = {REGRESSION}, description = "Verify logout functionality")
    public void shouldLogoutSuccessfully() {
        // Open navigation drawer
        driver.click("mobile.dashboard.menu.button");
        sleep(500);
        
        // Click logout
        driver.click("mobile.dashboard.logout.button");
        
        // Wait for logout
        sleep(1000);
        
        // Verify back on login screen
        Assert.assertTrue(driver.isVisible("mobile.login.username.field"),
            "Should be back on login screen after logout");
    }
}