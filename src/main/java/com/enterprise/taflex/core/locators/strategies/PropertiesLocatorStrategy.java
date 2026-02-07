package com.enterprise.taflex.core.locators.strategies;

import com.enterprise.taflex.core.config.ConfigManager;
import com.enterprise.taflex.core.exceptions.LocatorException;
import com.enterprise.taflex.core.locators.LocatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Locator strategy that loads locators from .properties files.
 * Supports hierarchical loading: global → mode-specific → page-specific
 */
public class PropertiesLocatorStrategy implements LocatorStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertiesLocatorStrategy.class);
    private static final String LOCATORS_BASE_PATH = "src/test/resources/locators/";
    
    private final Map<String, String> locatorCache;
    private final String executionMode;
    private String sourcePath;
    
    public PropertiesLocatorStrategy() {
        this.locatorCache = new HashMap<>();
        this.executionMode = ConfigManager.getExecutionMode();
        loadAllLocators();
    }
    
    /**
     * Load locators from all relevant properties files
     */
    private void loadAllLocators() {
        logger.info("Loading locators for execution mode: {}", executionMode);
        
        try {
            // 1. Load global locators (common across all modes)
            loadLocatorFile(LOCATORS_BASE_PATH + "global.properties");
            
            // 2. Load mode-specific locators
            loadLocatorFile(LOCATORS_BASE_PATH + executionMode + "/common.properties");
            
            // 3. Load all page-specific locators for this mode
            loadAllPageLocators();
            
            logger.info("Loaded {} locators total", locatorCache.size());
            
        } catch (Exception e) {
            logger.error("Failed to load locators", e);
            throw new LocatorException("Failed to initialize locator strategy", e);
        }
    }
    
    /**
     * Load all page-specific locator files from mode directory
     */
    private void loadAllPageLocators() {
        Path modeDir = Paths.get(LOCATORS_BASE_PATH + executionMode);
        
        if (!Files.exists(modeDir)) {
            logger.warn("Mode directory does not exist: {}", modeDir);
            return;
        }
        
        try (Stream<Path> paths = Files.list(modeDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".properties"))
                 .filter(p -> !p.getFileName().toString().equals("common.properties"))
                 .forEach(p -> loadLocatorFile(p.toString()));
        } catch (IOException e) {
            logger.error("Error loading page locators from: {}", modeDir, e);
        }
    }
    
    /**
     * Load a single properties file
     */
    private void loadLocatorFile(String filePath) {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            logger.debug("Locator file not found (skipping): {}", filePath);
            return;
        }
        
        try (InputStream input = new FileInputStream(path.toFile())) {
            Properties props = new Properties();
            props.load(input);
            
            // Add to cache (later files override earlier ones)
            props.forEach((key, value) -> {
                String keyStr = key.toString();
                String valueStr = value.toString();
                locatorCache.put(keyStr, valueStr);
                logger.debug("Loaded locator: {} = {}", keyStr, valueStr);
            });
            
            logger.info("Loaded {} locators from: {}", props.size(), filePath);
            
        } catch (IOException e) {
            logger.error("Error loading locator file: {}", filePath, e);
            throw new LocatorException("Failed to load locators from: " + filePath, e);
        }
    }
    
    @Override
    public String resolve(String logicalName) {
        if (!locatorCache.containsKey(logicalName)) {
            throw new LocatorException(logicalName, "Locator not found in any properties file");
        }
        return locatorCache.get(logicalName);
    }
    
    @Override
    public void load(String sourcePath) {
        this.sourcePath = sourcePath;
        loadLocatorFile(sourcePath);
    }
    
    @Override
    public boolean hasLocator(String logicalName) {
        return locatorCache.containsKey(logicalName);
    }
    
    @Override
    public void reload() {
        logger.info("Reloading locators...");
        locatorCache.clear();
        loadAllLocators();
    }
    
    @Override
    public String getSourceType() {
        return "properties";
    }
    
    /**
     * Get all loaded locators (for debugging)
     */
    public Map<String, String> getAllLocators() {
        return new HashMap<>(locatorCache);
    }
    
    /**
     * Get locators matching a pattern
     */
    public Map<String, String> getLocatorsByPattern(String pattern) {
        return locatorCache.entrySet().stream()
            .filter(e -> e.getKey().contains(pattern))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}