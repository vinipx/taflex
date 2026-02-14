package io.github.vinipx.taflex.core.locators;

import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.locators.strategies.JsonLocatorStrategy;
import io.github.vinipx.taflex.core.locators.strategies.PropertiesLocatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating LocatorStrategy instances.
 * Defaults to properties but supports JSON via configuration.
 */
public class LocatorFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(LocatorFactory.class);
    private static final Map<String, LocatorStrategy> strategyCache = new HashMap<>();
    
    private LocatorFactory() {}
    
    public static LocatorStrategy getLocatorStrategy() {
        String type = ConfigManager.getProperty("locator.strategy", "properties");
        return getLocatorStrategy(type);
    }
    
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
            case "json":
                strategy = new JsonLocatorStrategy();
                break;
            default:
                logger.warn("Unknown locator strategy type: {}. Defaulting to properties.", strategyType);
                strategy = new PropertiesLocatorStrategy();
        }
        
        strategyCache.put(cacheKey, strategy);
        return strategy;
    }
    
    public static void clearCache() {
        strategyCache.clear();
    }
}
