package io.github.vinipx.taflex.core.locators;

import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.locators.strategies.PropertiesLocatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating LocatorStrategy instances.
 * Supports caching and multiple locator sources.
 */
public class LocatorFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(LocatorFactory.class);
    private static final Map<String, LocatorStrategy> strategyCache = new HashMap<>();
    
    private LocatorFactory() {}
    
    /**
     * Get default locator strategy (Properties-based)
     * @return LocatorStrategy instance
     */
    public static LocatorStrategy getLocatorStrategy() {
        String cacheKey = "default_" + ConfigManager.getExecutionMode();
        
        if (!strategyCache.containsKey(cacheKey)) {
            logger.info("Creating new PropertiesLocatorStrategy");
            LocatorStrategy strategy = new PropertiesLocatorStrategy();
            strategyCache.put(cacheKey, strategy);
        }
        
        return strategyCache.get(cacheKey);
    }
    
    /**
     * Get locator strategy by type
     * @param strategyType Type of strategy (e.g., "properties", "database")
     * @return LocatorStrategy instance
     */
    public static LocatorStrategy getLocatorStrategy(String strategyType) {
        String cacheKey = strategyType + "_" + ConfigManager.getExecutionMode();
        
        if (strategyCache.containsKey(cacheKey)) {
            return strategyCache.get(cacheKey);
        }
        
        LocatorStrategy strategy;
        
        switch (strategyType.toLowerCase()) {
            case "properties":
                strategy = new PropertiesLocatorStrategy();
                break;
            default:
                throw new IllegalArgumentException("Unknown locator strategy type: " + strategyType);
        }
        
        strategyCache.put(cacheKey, strategy);
        return strategy;
    }
    
    /**
     * Clear all cached strategies
     */
    public static void clearCache() {
        logger.info("Clearing locator strategy cache");
        strategyCache.clear();
    }
    
    /**
     * Reload all cached strategies
     */
    public static void reloadAll() {
        logger.info("Reloading all locator strategies");
        for (LocatorStrategy strategy : strategyCache.values()) {
            strategy.reload();
        }
    }
}