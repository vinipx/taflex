package io.github.vinipx.taflex.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Central configuration manager for the test automation framework.
 * Loads and provides access to automation.properties.
 * Prioritizes System Environment Variables over file-based properties.
 */
public class ConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "automation.properties";
    private static final String DEFAULT_CONFIG_FILE = "automation.properties.template";
    
    private static ConfigManager instance;
    private Properties properties;
    
    private ConfigManager() {
        loadProperties();
        validateConfig();
    }
    
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        
        loadDefaultProperties();

        Path configPath = Paths.get(CONFIG_FILE);
        if (Files.exists(configPath)) {
            try (InputStream input = new FileInputStream(configPath.toFile())) {
                properties.load(input);
                logger.info("Loaded configuration from {}", CONFIG_FILE);
            } catch (IOException e) {
                logger.error("Failed to load {}", CONFIG_FILE, e);
            }
        }
        
        System.getProperties().forEach((key, value) -> 
            properties.setProperty(key.toString(), value.toString()));

        System.getenv().forEach((key, value) -> {
            properties.setProperty(key.toLowerCase().replace("_", "."), value);
            properties.setProperty(key, value);
        });
    }
    
    private void loadDefaultProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            logger.error("Failed to load default template", e);
        }
    }

    private void validateConfig() {
        String mode = getExecutionMode();
        if (!mode.matches("web|api|mobile")) {
            throw new IllegalStateException("Invalid execution.mode: " + mode + ". Must be web, api, or mobile.");
        }
    }
    
    public static String getProperty(String key) {
        return getInstance().properties.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
        return getInstance().properties.getProperty(key, defaultValue);
    }

    public static String requireProperty(String key) {
        String value = getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Required property missing: " + key);
        }
        return value.trim();
    }
    
    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        return (value == null) ? defaultValue : Integer.parseInt(value);
    }
    
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return (value == null) ? defaultValue : Boolean.parseBoolean(value);
    }
    
    public static String getExecutionMode() {
        return getProperty("execution.mode", "web").toLowerCase();
    }
    
    public static boolean isHeadless() {
        return getBooleanProperty("web.headless", true);
    }
    
    public static int getTimeout() {
        return getIntProperty("web.timeout", 30000);
    }
    
    public static String getBaseUrl() {
        return getProperty("web.base.url");
    }
    
    public static String getApiBaseUrl() {
        return getProperty("api.base.url");
    }
    
    public static void reload() {
        instance = new ConfigManager();
        logger.info("Configuration reloaded");
    }
}
