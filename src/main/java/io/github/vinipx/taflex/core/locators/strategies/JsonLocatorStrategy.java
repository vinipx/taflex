package io.github.vinipx.taflex.core.locators.strategies;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.exceptions.LocatorException;
import io.github.vinipx.taflex.core.locators.LocatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Locator strategy that loads and manages selectors from hierarchical JSON files.
 *
 * <p>This implementation supports merging multiple JSON files into a flat cache.
 * It follows a fallback hierarchy:
 * <ol>
 *     <li>global.json (common across all platforms)</li>
 *     <li>[mode]/common.json (common for specific mode like web/mobile)</li>
 *     <li>[mode]/[page].json (specific to a page or feature)</li>
 * </ol>
 */
public class JsonLocatorStrategy implements LocatorStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonLocatorStrategy.class);
    private static final String LOCATORS_BASE_PATH = "src/test/resources/locators/";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> locatorCache = new HashMap<>();
    private final String executionMode;

    /**
     * Initializes the JSON strategy and performs an initial reload of locators.
     */
    public JsonLocatorStrategy() {
        this.executionMode = ConfigManager.getExecutionMode();
        initializeLocatorCache();
    }

    private void initializeLocatorCache() {
        locatorCache.clear();
        loadJsonFile(LOCATORS_BASE_PATH + "global.json");
        loadJsonFile(LOCATORS_BASE_PATH + executionMode + "/common.json");
    }

    @Override
    public String resolve(String logicalName) {
        if (!locatorCache.containsKey(logicalName)) {
            throw new LocatorException(logicalName, "Locator not found in JSON cache");
        }
        return locatorCache.get(logicalName);
    }

    /**
     * Loads locators for a specific page name, following the hierarchy rules.
     *
     * @param pageName The base name of the JSON file (without extension).
     */
    @Override
    public void load(String pageName) {
        loadJsonFile(LOCATORS_BASE_PATH + "global.json");
        loadJsonFile(LOCATORS_BASE_PATH + executionMode + "/common.json");
        loadJsonFile(LOCATORS_BASE_PATH + executionMode + "/" + pageName + ".json");
    }

    /**
     * Parses a single JSON file and adds its contents to the flat cache.
     * Overwrites existing keys if collisions occur.
     */
    private void loadJsonFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(path.toFile());
            flattenNode("", rootNode);
            logger.info("Loaded JSON locators from: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to parse locator JSON: {}", filePath, e);
        }
    }

    /**
     * Recursively flattens nested JSON objects into dot-notation keys.
     */
    private void flattenNode(String prefix, JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                flattenNode(key, entry.getValue());
            }
        } else if (node.isValueNode()) {
            locatorCache.put(prefix, node.asText());
        }
    }

    @Override
    public boolean hasLocator(String logicalName) {
        return locatorCache.containsKey(logicalName);
    }

    @Override
    public void reload() {
        logger.info("Reloading locators...");
        initializeLocatorCache();
    }

    @Override
    public String getSourceType() {
        return "json";
    }
}
