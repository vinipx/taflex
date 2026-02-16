package io.github.vinipx.taflex.core.locators.strategies;

import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.exceptions.LocatorException;
import io.github.vinipx.taflex.core.locators.LocatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Default locator strategy that loads selectors from standard Java {@code .properties} files.
 *
 * <p>This strategy automatically loads and merges all properties files found in the
 * relevant mode directory, supporting a clean hierarchy:
 * <ol>
 *     <li>global.properties</li>
 *     <li>[mode]/common.properties</li>
 *     <li>[mode]/*.properties (all other files in the mode directory)</li>
 * </ol>
 */
public class PropertiesLocatorStrategy implements LocatorStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertiesLocatorStrategy.class);
    private static final String LOCATORS_BASE_PATH = "src/test/resources/locators/";
    
    private final Map<String, String> locatorCache;
    private final String executionMode;
    
    @SuppressWarnings("PMD.UnusedPrivateField")
    private String sourcePath;
    
    /**
     * Initializes the strategy and triggers the full hierarchical loading sequence.
     */
    public PropertiesLocatorStrategy() {
        this.locatorCache = new HashMap<>();
        this.executionMode = ConfigManager.getExecutionMode();
        loadAllLocators();
    }
    
    /**
     * Orchestrates the loading of properties files in the correct priority order.
     */
    private void loadAllLocators() {
        logger.info("Loading locators for execution mode: {}", executionMode);
        
        try {
            // 1. Load global locators
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
     * Automatically discovers and loads all properties files in the mode directory.
     * Skips files that have already been loaded (like common.properties).
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
                 .filter(p -> !"common.properties".equals(p.getFileName().toString()))
                 .forEach(p -> loadLocatorFile(p.toString()));
        } catch (IOException e) {
            logger.error("Error loading page locators from: {}", modeDir, e);
        }
    }
    
    /**
     * Reads a single properties file and merges it into the local cache.
     *
     * @param filePath The path to the .properties file.
     */
    private void loadLocatorFile(String filePath) {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            logger.debug("Locator file not found (skipping): {}", filePath);
            return;
        }
        
        try (InputStream input = Files.newInputStream(path)) {
            Properties props = new Properties();
            props.load(input);
            
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
     * Retrieves a copy of the entire current locator cache.
     *
     * @return A map of all logical names to selectors.
     */
    @Override
    public Map<String, String> getAllLocators() {
        return new HashMap<>(locatorCache);
    }
    
    /**
     * Filters the cache for locators matching a partial logical name.
     *
     * @param pattern The substring to search for.
     * @return A map containing only matching entries.
     */
    public Map<String, String> getLocatorsByPattern(String pattern) {
        return locatorCache.entrySet().stream()
            .filter(e -> e.getKey().contains(pattern))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
