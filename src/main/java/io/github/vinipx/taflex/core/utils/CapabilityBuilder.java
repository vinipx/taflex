package io.github.vinipx.taflex.core.utils;

import io.github.vinipx.taflex.core.config.ConfigManager;
import org.openqa.selenium.MutableCapabilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Ported from taflex-js.
 * Builds capabilities for cloud providers like BrowserStack and SauceLabs.
 */
public class CapabilityBuilder {

    public static MutableCapabilities buildWebCapabilities() {
        String platform = ConfigManager.getProperty("cloud.platform", "local");
        MutableCapabilities capabilities = new MutableCapabilities();

        if ("browserstack".equalsIgnoreCase(platform)) {
            capabilities.setCapability("browserName", ConfigManager.getProperty("browser", "chromium"));
            Map<String, Object> bstackOptions = new HashMap<>();
            bstackOptions.put("userName", ConfigManager.getProperty("cloud.user"));
            bstackOptions.put("accessKey", ConfigManager.getProperty("cloud.key"));
            bstackOptions.put("os", ConfigManager.getProperty("os", "Windows"));
            bstackOptions.put("osVersion", ConfigManager.getProperty("os.version", "11"));
            capabilities.setCapability("bstack:options", bstackOptions);
        } else if ("saucelabs".equalsIgnoreCase(platform)) {
            capabilities.setCapability("browserName", ConfigManager.getProperty("browser", "chromium"));
            Map<String, Object> sauceOptions = new HashMap<>();
            sauceOptions.put("username", ConfigManager.getProperty("cloud.user"));
            sauceOptions.put("accessKey", ConfigManager.getProperty("cloud.key"));
            sauceOptions.put("platformName", ConfigManager.getProperty("os", "Windows 11"));
            capabilities.setCapability("sauce:options", sauceOptions);
        }

        return capabilities;
    }
}
