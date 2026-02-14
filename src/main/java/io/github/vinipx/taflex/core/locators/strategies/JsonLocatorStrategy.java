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
 * Locator strategy that loads locators from .json files.
 * Supports hierarchical merging: global.json -> [mode]/common.json -> [mode]/[page].json
 */
public class JsonLocatorStrategy implements LocatorStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonLocatorStrategy.class);
    private static final String LOCATORS_BASE_PATH = "src/test/resources/locators/";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> locatorCache = new HashMap<>();
    private final String executionMode;

    public JsonLocatorStrategy() {
        this.executionMode = ConfigManager.getExecutionMode();
        reload();
    }

    @Override
    public String resolve(String logicalName) {
        if (!locatorCache.containsKey(logicalName)) {
            throw new LocatorException(logicalName, "Locator not found in JSON cache");
        }
        return locatorCache.get(logicalName);
    }

    @Override
    public void load(String pageName) {
        // Hierarchical load: 
        // 1. global.json
        loadJsonFile(LOCATORS_BASE_PATH + "global.json");
        // 2. [mode]/common.json
        loadJsonFile(LOCATORS_BASE_PATH + executionMode + "/common.json");
        // 3. [mode]/[page].json
        loadJsonFile(LOCATORS_BASE_PATH + executionMode + "/" + pageName + ".json");
    }

    private void loadJsonFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return;

        try {
            JsonNode rootNode = objectMapper.readTree(path.toFile());
            flattenNode("", rootNode);
            logger.info("Loaded JSON locators from: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to parse locator JSON: {}", filePath, e);
        }
    }

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
        locatorCache.clear();
        loadJsonFile(LOCATORS_BASE_PATH + "global.json");
        loadJsonFile(LOCATORS_BASE_PATH + executionMode + "/common.json");
    }

    @Override
    public String getSourceType() {
        return "json";
    }
}
