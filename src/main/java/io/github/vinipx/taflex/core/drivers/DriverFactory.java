package io.github.vinipx.taflex.core.drivers;

import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.drivers.strategies.ApiDriverStrategy;
import io.github.vinipx.taflex.core.drivers.strategies.MobileDriverStrategy;
import io.github.vinipx.taflex.core.drivers.strategies.PlaywrightDriverStrategy;
import io.github.vinipx.taflex.core.exceptions.DriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Factory class for managing and providing {@code AutomationDriver} instances.
 *
 * <p>This factory implements the **Strategy Pattern** to decide at runtime which
 * driver implementation to instantiate based on the configured execution mode.
 * It also provides a caching mechanism to maintain singletons per mode across
 * the test execution lifecycle.
 */
public final class DriverFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    private static final Map<String, AutomationDriver> driverCache = new HashMap<>();
    
    /**
     * Private constructor to prevent instantiation of utility factory.
     */
    private DriverFactory() { }
    
    /**
     * Retrieves the driver instance for the default execution mode.
     * The mode is read from the {@code execution.mode} configuration property.
     *
     * @return An initialized (or cached) {@code AutomationDriver} instance.
     */
    public static AutomationDriver getDriver() {
        String mode = ConfigManager.getExecutionMode();
        return getDriver(mode);
    }
    
    /**
     * Retrieves or creates a driver for a specific execution mode.
     *
     * @param mode The desired execution mode: "web", "api", or "mobile".
     * @return The corresponding {@code AutomationDriver} instance.
     * @throws DriverException If the requested mode is unknown or unsupported.
     */
    public static AutomationDriver getDriver(String mode) {
        // Check cache first
        if (driverCache.containsKey(mode)) {
            logger.debug("Returning cached driver for mode: {}", mode);
            return driverCache.get(mode);
        }
        
        AutomationDriver driver;
        
        switch (mode.toLowerCase(Locale.ROOT)) {
            case "web":
                logger.info("Creating Web driver (Playwright)");
                driver = new PlaywrightDriverStrategy();
                break;
                
            case "api":
                logger.info("Creating API driver (HttpClient)");
                driver = new ApiDriverStrategy();
                break;
                
            case "mobile":
                logger.info("Creating Mobile driver (Appium)");
                driver = new MobileDriverStrategy();
                break;
                
            default:
                throw new DriverException("Unknown execution mode: " + mode +
                    ". Valid modes are: web, api, mobile");
        }
        
        // Cache the driver
        driverCache.put(mode, driver);
        return driver;
    }
    
    /**
     * Creates a new driver instance without checking or updating the cache.
     * Recommended for highly parallel scenarios where shared state is undesirable.
     *
     * @param mode The execution mode.
     * @return A new, uninitialized {@code AutomationDriver} instance.
     * @throws DriverException If the mode is unknown.
     */
    public static AutomationDriver getDriverWithoutCache(String mode) {
        AutomationDriver driver;
        
        switch (mode.toLowerCase(Locale.ROOT)) {
            case "web":
                driver = new PlaywrightDriverStrategy();
                break;
            case "api":
                driver = new ApiDriverStrategy();
                break;
            case "mobile":
                driver = new MobileDriverStrategy();
                break;
            default:
                throw new DriverException("Unknown execution mode: " + mode);
        }
        
        return driver;
    }
    
    /**
     * Terminates all cached drivers and clears the factory cache.
     * Typically called in a global teardown or suite listener.
     */
    public static void clearCache() {
        logger.info("Clearing driver cache");
        for (AutomationDriver driver : driverCache.values()) {
            try {
                driver.terminate();
            } catch (Exception e) {
                logger.warn("Error terminating driver during cache clear", e);
            }
        }
        driverCache.clear();
    }
    
    /**
     * Removes and terminates a specific driver instance from the cache.
     *
     * @param mode The execution mode associated with the driver to remove.
     */
    public static void removeFromCache(String mode) {
        AutomationDriver driver = driverCache.remove(mode);
        if (driver != null) {
            try {
                driver.terminate();
            } catch (Exception e) {
                logger.warn("Error terminating driver: {}", mode, e);
            }
        }
    }
}
