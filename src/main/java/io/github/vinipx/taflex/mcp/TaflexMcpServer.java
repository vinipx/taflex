package io.github.vinipx.taflex.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.locators.LocatorFactory;
import io.github.vinipx.taflex.core.locators.LocatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.vinipx.taflex.mcp.McpProtocol.*;

/**
 * TAFLEX MCP Server implementation.
 * Provides tools and resources to interact with the TAFLEX framework via Model Context Protocol.
 */
public class TaflexMcpServer {
    private static final Logger logger = LoggerFactory.getLogger(TaflexMcpServer.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, ToolHandler> toolHandlers = new HashMap<>();

    public static void main(String[] args) {
        new TaflexMcpServer().run();
    }

    public TaflexMcpServer() {
        registerTools();
    }

    private void registerTools() {
        toolHandlers.put("get_config", this::handleGetConfig);
        toolHandlers.put("list_locators", this::handleListLocators);
        toolHandlers.put("get_locator", this::handleGetLocator);
        toolHandlers.put("get_execution_mode", this::handleGetExecutionMode);
        toolHandlers.put("run_suite", this::handleRunSuite);
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Request request = mapper.readValue(line, Request.class);
                    processRequest(request);
                } catch (Exception e) {
                    logger.error("Error processing request: {}", line, e);
                    // Standard JSON-RPC error response would go here
                }
            }
        } catch (Exception e) {
            logger.error("MCP Server error", e);
        }
    }

    private void processRequest(Request request) throws Exception {
        if (request.method == null) return;

        switch (request.method) {
            case "initialize":
                sendResponse(request.id, createInitializeResult());
                break;
            case "notifications/initialized":
                // Client confirmed initialization
                break;
            case "tools/list":
                sendResponse(request.id, new ToolListResponse(getTools()));
                break;
            case "tools/call":
                handleToolCall(request);
                break;
            default:
                sendError(request.id, -32601, "Method not found: " + request.method);
        }
    }

    private Object createInitializeResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", "2024-11-05");
        result.put("capabilities", Collections.singletonMap("tools", Collections.emptyMap()));
        Map<String, String> serverInfo = new HashMap<>();
        serverInfo.put("name", "taflex-mcp-server");
        serverInfo.put("version", "1.0.0");
        result.put("serverInfo", serverInfo);
        return result;
    }

    private List<Tool> getTools() {
        List<Tool> tools = new ArrayList<>();
        
        ObjectNode emptySchema = mapper.createObjectNode();
        emptySchema.put("type", "object");
        emptySchema.set("properties", mapper.createObjectNode());

        tools.add(new Tool("get_config", "Retrieve current TAFLEX configuration properties", emptySchema));
        tools.add(new Tool("get_execution_mode", "Get current execution mode (web, api, mobile)", emptySchema));
        
        ObjectNode listLocatorsSchema = mapper.createObjectNode();
        listLocatorsSchema.put("type", "object");
        ObjectNode listLocatorsProps = mapper.createObjectNode();
        ObjectNode patternProp = mapper.createObjectNode();
        patternProp.put("type", "string");
        patternProp.put("description", "Optional pattern to filter locators by name");
        listLocatorsProps.set("pattern", patternProp);
        listLocatorsSchema.set("properties", listLocatorsProps);
        tools.add(new Tool("list_locators", "List all available logical locators", listLocatorsSchema));

        ObjectNode getLocatorSchema = mapper.createObjectNode();
        getLocatorSchema.put("type", "object");
        ObjectNode getLocatorProps = mapper.createObjectNode();
        ObjectNode nameProp = mapper.createObjectNode();
        nameProp.put("type", "string");
        nameProp.put("description", "The logical name of the locator");
        getLocatorProps.set("name", nameProp);
        getLocatorSchema.set("properties", getLocatorProps);
        getLocatorSchema.set("required", mapper.createArrayNode().add("name"));
        tools.add(new Tool("get_locator", "Get details of a specific locator", getLocatorSchema));

        ObjectNode runSuiteSchema = mapper.createObjectNode();
        runSuiteSchema.put("type", "object");
        ObjectNode runSuiteProps = mapper.createObjectNode();
        ObjectNode suiteProp = mapper.createObjectNode();
        suiteProp.put("type", "string");
        suiteProp.put("description", "The name of the suite to run (e.g., api-suite, smoke-suite)");
        runSuiteProps.set("suite", suiteProp);
        runSuiteSchema.set("properties", runSuiteProps);
        runSuiteSchema.set("required", mapper.createArrayNode().add("suite"));
        tools.add(new Tool("run_suite", "Execute a specific TestNG suite", runSuiteSchema));

        return tools;
    }

    private void handleToolCall(Request request) throws Exception {
        String toolName = (String) request.params.get("name");
        Map<String, Object> arguments = (Map<String, Object>) request.params.get("arguments");
        if (arguments == null) arguments = Collections.emptyMap();

        ToolHandler handler = toolHandlers.get(toolName);
        if (handler != null) {
            try {
                CallToolResponse response = handler.handle(arguments);
                sendResponse(request.id, response);
            } catch (Exception e) {
                sendResponse(request.id, new CallToolResponse(Collections.singletonList(new Content("Error: " + e.getMessage()))));
            }
        } else {
            sendError(request.id, -32601, "Tool not found: " + toolName);
        }
    }

    private CallToolResponse handleGetConfig(Map<String, Object> args) {
        // Since ConfigManager doesn't expose all properties easily in a map, 
        // we'll just return a message or some key ones for now.
        // In a real implementation we might want to expose more.
        StringBuilder sb = new StringBuilder("Current TAFLEX Configuration:\n");
        sb.append("Execution Mode: ").append(ConfigManager.getExecutionMode()).append("\n");
        sb.append("Base URL: ").append(ConfigManager.getBaseUrl()).append("\n");
        sb.append("API Base URL: ").append(ConfigManager.getApiBaseUrl()).append("\n");
        sb.append("Headless: ").append(ConfigManager.isHeadless()).append("\n");
        sb.append("Timeout: ").append(ConfigManager.getTimeout()).append("\n");
        
        return new CallToolResponse(Collections.singletonList(new Content(sb.toString())));
    }

    private CallToolResponse handleGetExecutionMode(Map<String, Object> args) {
        return new CallToolResponse(Collections.singletonList(new Content("Current mode: " + ConfigManager.getExecutionMode())));
    }

    private CallToolResponse handleListLocators(Map<String, Object> args) {
        String pattern = (String) args.get("pattern");
        LocatorStrategy strategy = LocatorFactory.getLocatorStrategy();
        Map<String, String> all = strategy.getAllLocators();
        
        StringBuilder sb = new StringBuilder("Available Locators:\n");
        all.forEach((k, v) -> {
            if (pattern == null || k.contains(pattern)) {
                sb.append("- ").append(k).append("\n");
            }
        });
        
        return new CallToolResponse(Collections.singletonList(new Content(sb.toString())));
    }

    private CallToolResponse handleGetLocator(Map<String, Object> args) {
        String name = (String) args.get("name");
        if (name == null) return new CallToolResponse(Collections.singletonList(new Content("Error: name is required")));
        
        LocatorStrategy strategy = LocatorFactory.getLocatorStrategy();
        try {
            String value = strategy.resolve(name);
            return new CallToolResponse(Collections.singletonList(new Content(name + " = " + value)));
        } catch (Exception e) {
            return new CallToolResponse(Collections.singletonList(new Content("Locator not found: " + name)));
        }
    }

    private CallToolResponse handleRunSuite(Map<String, Object> args) {
        String suite = (String) args.get("suite");
        if (suite == null) return new CallToolResponse(Collections.singletonList(new Content("Error: suite is required")));

        try {
            ProcessBuilder pb = new ProcessBuilder("./gradlew", "test", "-Dsuite=" + suite);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            String result = exitCode == 0 ? "SUCCESS" : "FAILED";
            
            return new CallToolResponse(Collections.singletonList(new Content("Suite " + suite + " finished with " + result + "\n\nOutput:\n" + output.toString())));
        } catch (Exception e) {
            return new CallToolResponse(Collections.singletonList(new Content("Error running suite: " + e.getMessage())));
        }
    }

    private void sendResponse(Object id, Object result) throws Exception {
        Response response = new Response(id, result);
        System.out.println(mapper.writeValueAsString(response));
        System.out.flush();
    }

    private void sendError(Object id, int code, String message) throws Exception {
        Response response = new Response();
        response.id = id;
        response.error = new McpProtocol.Error(code, message);
        System.out.println(mapper.writeValueAsString(response));
        System.out.flush();
    }

    @FunctionalInterface
    interface ToolHandler {
        CallToolResponse handle(Map<String, Object> arguments) throws Exception;
    }
}
