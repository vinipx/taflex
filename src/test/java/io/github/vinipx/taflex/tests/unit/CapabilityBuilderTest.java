package io.github.vinipx.taflex.tests.unit;

import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.utils.CapabilityBuilder;
import org.openqa.selenium.MutableCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.Map;

public class CapabilityBuilderTest {

    private static final String CLOUD_PLATFORM = "cloud.platform";

    @AfterMethod
    public void tearDown() {
        System.clearProperty(CLOUD_PLATFORM);
        System.clearProperty("browser");
        System.clearProperty("cloud.user");
        System.clearProperty("cloud.key");
        System.clearProperty("os");
        System.clearProperty("os.version");
        ConfigManager.reload();
    }

    @Test
    public void testBuildLocalCapabilities() {
        System.setProperty(CLOUD_PLATFORM, "local");
        ConfigManager.reload();
        
        MutableCapabilities capabilities = CapabilityBuilder.buildWebCapabilities();
        Assert.assertNotNull(capabilities);
        Assert.assertTrue(capabilities.asMap().isEmpty(), "Local capabilities should be empty by default");
    }

    @Test
    public void testBuildBrowserStackCapabilities() {
        System.setProperty(CLOUD_PLATFORM, "browserstack");
        System.setProperty("browser", "firefox");
        System.setProperty("cloud.user", "testuser");
        System.setProperty("cloud.key", "testkey");
        System.setProperty("os", "OS X");
        System.setProperty("os.version", "Ventura");
        ConfigManager.reload();

        MutableCapabilities capabilities = CapabilityBuilder.buildWebCapabilities();
        Map<String, Object> capsMap = capabilities.asMap();
        
        Assert.assertEquals(capabilities.getCapability("browserName"), "firefox");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> bstackOptions = (Map<String, Object>) capsMap.get("bstack:options");
        Assert.assertNotNull(bstackOptions);
        Assert.assertEquals(bstackOptions.get("userName"), "testuser");
        Assert.assertEquals(bstackOptions.get("accessKey"), "testkey");
        Assert.assertEquals(bstackOptions.get("os"), "OS X");
        Assert.assertEquals(bstackOptions.get("osVersion"), "Ventura");
    }

    @Test
    public void testBuildSauceLabsCapabilities() {
        System.setProperty(CLOUD_PLATFORM, "saucelabs");
        System.setProperty("browser", "webkit");
        System.setProperty("cloud.user", "sauceuser");
        System.setProperty("cloud.key", "saucekey");
        System.setProperty("os", "Linux");
        ConfigManager.reload();

        MutableCapabilities capabilities = CapabilityBuilder.buildWebCapabilities();
        Map<String, Object> capsMap = capabilities.asMap();
        
        Assert.assertEquals(capabilities.getCapability("browserName"), "webkit");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> sauceOptions = (Map<String, Object>) capsMap.get("sauce:options");
        Assert.assertNotNull(sauceOptions);
        Assert.assertEquals(sauceOptions.get("username"), "sauceuser");
        Assert.assertEquals(sauceOptions.get("accessKey"), "saucekey");
        Assert.assertEquals(sauceOptions.get("platformName"), "Linux");
    }
}
