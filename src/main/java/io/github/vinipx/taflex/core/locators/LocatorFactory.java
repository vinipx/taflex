package io.github.vinipx.taflex.core.locators;

import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.locators.strategies.JsonLocatorStrategy;
import io.github.vinipx.taflex.core.locators.strategies.PropertiesLocatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Factory class for instantiating and caching {@link LocatorStrategy} implementations.
 *
 * <p>By default, the framework uses {@link PropertiesLocatorStrategy}. This behavior
 * can be changed via the {@code locator.strategy} configuration property.
 */
public final class LocatorFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(LocatorFactory.class);
    private static final Map<String, LocatorStrategy> strategyCache = new HashMap<>();
    
    /**
     * Private constructor to prevent instantiation.
     */
    private LocatorFactory() { }
    
    /**
     * Retrieves the configured locator strategy for the current execution mode.
     * Reads the strategy type from the {@code locator.strategy} property.
     *
     * @return A cached {@code LocatorStrategy} instance.
     */
    public static LocatorStrategy getLocatorStrategy() {
        String type = ConfigManager.getProperty("locator.strategy", "properties");
        return getLocatorStrategy(type);
    }
    
    /**
     * Retrieves or creates a locator strategy of a specific type.
     *
     * @param strategyType The type identifier ("properties" or "json").
     * @return The corresponding strategy instance.
     */
    public static LocatorStrategy getLocatorStrategy(String strategyType) {
        String cacheKey = strategyType + "_" + ConfigManager.getExecutionMode();
        
        if (strategyCache.containsKey(cacheKey)) {
            return strategyCache.get(cacheKey);
        }
        
        LocatorStrategy strategy;
        
        switch (strategyType.toLowerCase(Locale.ROOT)) {
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
    
    /**
     * Clears all cached locator strategies.
     */
    public static void clearCache() {
        strategyCache.clear();
    }
}
