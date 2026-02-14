package io.github.vinipx.taflex.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

/**
 * Central configuration manager for the TAFLEX framework.
 *
 * <p>This manager is responsible for loading and providing access to framework settings.
 * It follows a specific priority order for property resolution:
 * <ol>
 *     <li>System Environment Variables (highest priority)</li>
 *     <li>System Properties (-Dkey=value)</li>
 *     <li>automation.properties file in the project root</li>
 *     <li>automation.properties.template (default values)</li>
 * </ol>
 *
 * <p>Environment variables are automatically mapped to property keys by converting
 * them to lowercase and replacing underscores with dots (e.g., {@code EXECUTION_MODE}
 * becomes {@code execution.mode}).
 */
public final class ConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "automation.properties";
    private static final String DEFAULT_CONFIG_FILE = "automation.properties.template";
    
    private static ConfigManager instance;
    private Properties properties;
    
    /**
     * Private constructor to enforce Singleton pattern.
     * Triggers the property loading sequence.
     */
    private ConfigManager() {
        loadProperties();
    }
    
    /**
     * Returns the singleton instance of the ConfigManager.
     *
     * @return The ConfigManager instance.
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
            instance.validateConfig();
        }
        return instance;
    }
    
    /**
     * Loads properties from all supported sources in the defined priority order.
     */
    private void loadProperties() {
        properties = new Properties();
        
        loadDefaultProperties();

        Path configPath = Paths.get(CONFIG_FILE);
        if (Files.exists(configPath)) {
            try (InputStream input = Files.newInputStream(configPath)) {
                properties.load(input);
                logger.info("Loaded configuration from {}", CONFIG_FILE);
            } catch (IOException e) {
                logger.error("Failed to load {}", CONFIG_FILE, e);
            }
        }
        
        System.getProperties().forEach((key, value) -> 
            properties.setProperty(key.toString(), value.toString()));

        System.getenv().forEach((key, value) -> {
            properties.setProperty(key.toLowerCase(Locale.ROOT).replace("_", "."), value);
            properties.setProperty(key, value);
        });
    }
    
    /**
     * Loads default properties from the template file bundled in the classpath.
     */
    private void loadDefaultProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            logger.error("Failed to load default template", e);
        }
    }

    /**
     * Validates that the core configuration parameters are present and valid.
     *
     * @throws IllegalStateException If the execution mode is invalid.
     */
    private void validateConfig() {
        String mode = getExecutionMode();
        if (!mode.matches("web|api|mobile")) {
            throw new IllegalStateException("Invalid execution.mode: " + mode + ". Must be web, api, or mobile.");
        }
    }
    
    /**
     * Retrieves a property value by its key.
     *
     * @param key The property key.
     * @return The property value or null if not found.
     */
    public static String getProperty(String key) {
        return getInstance().properties.getProperty(key);
    }
    
    /**
     * Retrieves a property value by its key, with a default fallback.
     *
     * @param key          The property key.
     * @param defaultValue The value to return if the key is not found.
     * @return The property value or defaultValue.
     */
    public static String getProperty(String key, String defaultValue) {
        return getInstance().properties.getProperty(key, defaultValue);
    }

    /**
     * Retrieves a property value and ensures it is not null or empty.
     *
     * @param key The property key.
     * @return The trimmed property value.
     * @throws IllegalArgumentException If the property is missing or blank.
     */
    public static String requireProperty(String key) {
        String value = getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Required property missing: " + key);
        }
        return value.trim();
    }
    
    /**
     * Retrieves a property as an integer.
     *
     * @param key          The property key.
     * @param defaultValue Fallback value if property is missing or invalid.
     * @return The parsed integer value.
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return (value == null) ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for property {}: {}. Using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Retrieves a property as a boolean.
     *
     * @param key          The property key.
     * @param defaultValue Fallback value.
     * @return true if property is "true" (case-insensitive), false otherwise.
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return (value == null) ? defaultValue : Boolean.parseBoolean(value);
    }
    
    /**
     * Gets the current execution mode (web, api, or mobile).
     * Defaults to "web".
     *
     * @return The lowercase execution mode string.
     */
    public static String getExecutionMode() {
        return getProperty("execution.mode", "web").toLowerCase(Locale.ROOT);
    }
    
    /**
     * Checks if the web driver should run in headless mode.
     *
     * @return true if headless, false otherwise.
     */
    public static boolean isHeadless() {
        return getBooleanProperty("web.headless", true);
    }
    
    /**
     * Gets the configured global timeout for driver operations.
     *
     * @return Timeout in milliseconds.
     */
    public static int getTimeout() {
        return getIntProperty("web.timeout", 30_000);
    }
    
    /**
     * Gets the base URL for web automation.
     *
     * @return The configured web base URL.
     */
    public static String getBaseUrl() {
        return getProperty("web.base.url");
    }
    
    /**
     * Gets the base URL for API automation.
     *
     * @return The configured API base URL.
     */
    public static String getApiBaseUrl() {
        return getProperty("api.base.url");
    }
    
    /**
     * Reloads the configuration from files and environment.
     * Useful if properties are changed dynamically during execution.
     */
    public static void reload() {
        instance = new ConfigManager();
        logger.info("Configuration reloaded");
    }
}
