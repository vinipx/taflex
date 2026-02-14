package io.github.vinipx.taflex.core.drivers;

import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.drivers.strategies.ApiDriverStrategy;
import io.github.vinipx.taflex.core.drivers.strategies.MobileDriverStrategy;
import io.github.vinipx.taflex.core.drivers.strategies.PlaywrightDriverStrategy;
import io.github.vinipx.taflex.core.exceptions.DriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating AutomationDriver instances based on execution mode.
 * Implements the Strategy pattern for runtime driver resolution.
 */
public class DriverFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    private static final Map<String, AutomationDriver> driverCache = new HashMap<>();
    
    /**
     * Private constructor to prevent instantiation
     */
    private DriverFactory() { }
    
    /**
     * Get driver based on execution mode from automation.properties
     * @return AutomationDriver instance
     */
    public static AutomationDriver getDriver() {
        String mode = ConfigManager.getExecutionMode();
        return getDriver(mode);
    }
    
    /**
     * Get driver for specific execution mode
     * @param mode Execution mode: "web", "api", or "mobile"
     * @return AutomationDriver instance
     */
    public static AutomationDriver getDriver(String mode) {
        // Check cache first
        if (driverCache.containsKey(mode)) {
            logger.debug("Returning cached driver for mode: {}", mode);
            return driverCache.get(mode);
        }
        
        AutomationDriver driver;
        
        switch (mode.toLowerCase()) {
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
     * Get driver without caching (for parallel execution)
     * @param mode Execution mode
     * @return New AutomationDriver instance
     */
    public static AutomationDriver getDriverWithoutCache(String mode) {
        AutomationDriver driver;
        
        switch (mode.toLowerCase()) {
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
     * Clear driver cache (useful for test cleanup)
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
     * Remove specific driver from cache
     * @param mode Execution mode
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