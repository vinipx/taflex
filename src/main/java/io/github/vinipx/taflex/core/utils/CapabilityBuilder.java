package io.github.vinipx.taflex.core.utils;

import io.github.vinipx.taflex.core.config.ConfigManager;
import org.openqa.selenium.MutableCapabilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for constructing remote execution capabilities.
 *
 * <p>Handles the creation of provider-specific capability objects for cloud grids
 * like BrowserStack and SauceLabs based on framework configuration.
 */
public final class CapabilityBuilder {

    /**
     * Private constructor to prevent instantiation.
     */
    private CapabilityBuilder() { }

    /**
     * Builds and returns web capabilities for the configured cloud platform.
     *
     * <p>If the platform is "local", an empty set of capabilities is returned.
     * Supported platforms include:
     * <ul>
     *     <li>browserstack</li>
     *     <li>saucelabs</li>
     * </ul>
     *
     * @return A configured {@link MutableCapabilities} instance.
     */
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
