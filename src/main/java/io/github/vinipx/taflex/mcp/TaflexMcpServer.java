package io.github.vinipx.taflex.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.locators.LocatorFactory;
import io.github.vinipx.taflex.core.locators.LocatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    private static final int METHOD_NOT_FOUND_CODE = -32_601;
    private static final String JSON_TYPE_OBJECT = "object";
    private static final String JSON_TYPE_STRING = "string";
    private static final String PROP_NAME = "name";
    private static final String PROP_TYPE = "type";
    private static final String PROP_PROPERTIES = "properties";

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, ToolHandler> toolHandlers = new HashMap<>();

    public static void main(String[] args) {
        new TaflexMcpServer().run();
    }

    public TaflexMcpServer() {
        registerTools();
    }

    private void registerTools() {
        toolHandlers.put("get_config", args -> handleGetConfig());
        toolHandlers.put("list_locators", this::handleListLocators);
        toolHandlers.put("get_locator", this::handleGetLocator);
        toolHandlers.put("get_execution_mode", args -> handleGetExecutionMode());
        toolHandlers.put("run_suite", this::handleRunSuite);
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Request request = mapper.readValue(line, Request.class);
                    processRequest(request);
                } catch (IOException e) {
                    logger.error("Error processing request: {}", line, e);
                }
            }
        } catch (IOException e) {
            logger.error("MCP Server error", e);
        }
    }

    private void processRequest(Request request) {
        if (request.method == null) {
            return;
        }

        try {
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
                    sendError(request.id, METHOD_NOT_FOUND_CODE, "Method not found: " + request.method);
            }
        } catch (IOException e) {
            logger.error("Error sending response for method: {}", request.method, e);
        }
    }

    private Object createInitializeResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", "2024-11-05");
        result.put("capabilities", Collections.singletonMap("tools", Collections.emptyMap()));
        Map<String, String> serverInfo = new HashMap<>();
        serverInfo.put(PROP_NAME, "taflex-mcp-server");
        serverInfo.put("version", "1.0.0");
        result.put("serverInfo", serverInfo);
        return result;
    }

    private List<McpTool> getTools() {
        List<McpTool> tools = new ArrayList<>();
        
        ObjectNode emptySchema = mapper.createObjectNode();
        emptySchema.put(PROP_TYPE, JSON_TYPE_OBJECT);
        emptySchema.set(PROP_PROPERTIES, mapper.createObjectNode());

        tools.add(new McpTool("get_config", "Retrieve current TAFLEX configuration properties", emptySchema));
        tools.add(new McpTool("get_execution_mode", "Get current execution mode (web, api, mobile)", emptySchema));
        
        ObjectNode listLocatorsSchema = mapper.createObjectNode();
        listLocatorsSchema.put(PROP_TYPE, JSON_TYPE_OBJECT);
        ObjectNode listLocatorsProps = mapper.createObjectNode();
        ObjectNode patternProp = mapper.createObjectNode();
        patternProp.put(PROP_TYPE, JSON_TYPE_STRING);
        patternProp.put("description", "Optional pattern to filter locators by name");
        listLocatorsProps.set("pattern", patternProp);
        listLocatorsSchema.set(PROP_PROPERTIES, listLocatorsProps);
        tools.add(new McpTool("list_locators", "List all available logical locators", listLocatorsSchema));

        ObjectNode getLocatorSchema = mapper.createObjectNode();
        getLocatorSchema.put(PROP_TYPE, JSON_TYPE_OBJECT);
        ObjectNode getLocatorProps = mapper.createObjectNode();
        ObjectNode nameProp = mapper.createObjectNode();
        nameProp.put(PROP_TYPE, JSON_TYPE_STRING);
        nameProp.put("description", "The logical name of the locator");
        getLocatorProps.set(PROP_NAME, nameProp);
        getLocatorSchema.set(PROP_PROPERTIES, getLocatorProps);
        getLocatorSchema.set("required", mapper.createArrayNode().add(PROP_NAME));
        tools.add(new McpTool("get_locator", "Get details of a specific locator", getLocatorSchema));

        ObjectNode runSuiteSchema = mapper.createObjectNode();
        runSuiteSchema.put(PROP_TYPE, JSON_TYPE_OBJECT);
        ObjectNode runSuiteProps = mapper.createObjectNode();
        ObjectNode suiteProp = mapper.createObjectNode();
        suiteProp.put(PROP_TYPE, JSON_TYPE_STRING);
        suiteProp.put("description", "The name of the suite to run (e.g., api-suite, smoke-suite)");
        runSuiteProps.set("suite", suiteProp);
        runSuiteSchema.set(PROP_PROPERTIES, runSuiteProps);
        runSuiteSchema.set("required", mapper.createArrayNode().add("suite"));
        tools.add(new McpTool("run_suite", "Execute a specific TestNG suite", runSuiteSchema));

        return tools;
    }

    @SuppressWarnings("unchecked")
    private void handleToolCall(Request request) throws IOException {
        String toolName = (String) request.params.get(PROP_NAME);
        Map<String, Object> arguments = (Map<String, Object>) request.params.get("arguments");
        if (arguments == null) {
            arguments = Collections.emptyMap();
        }

        ToolHandler handler = toolHandlers.get(toolName);
        if (handler != null) {
            try {
                CallToolResponse response = handler.handle(arguments);
                sendResponse(request.id, response);
            } catch (Exception e) {
                Content content = new Content("Error: " + e.getMessage());
                sendResponse(request.id, new CallToolResponse(Collections.singletonList(content)));
            }
        } else {
            sendError(request.id, METHOD_NOT_FOUND_CODE, "Tool not found: " + toolName);
        }
    }

    private CallToolResponse handleGetConfig() {
        String configMsg = String.format("Current TAFLEX Configuration:\n" +
                "Execution Mode: %s\n" +
                "Base URL: %s\n" +
                "API Base URL: %s\n" +
                "Headless: %s\n" +
                "Timeout: %d\n",
                ConfigManager.getExecutionMode(),
                ConfigManager.getBaseUrl(),
                ConfigManager.getApiBaseUrl(),
                ConfigManager.isHeadless(),
                ConfigManager.getTimeout());
        
        return new CallToolResponse(Collections.singletonList(new Content(configMsg)));
    }

    private CallToolResponse handleGetExecutionMode() {
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
        String name = (String) args.get(PROP_NAME);
        if (name == null) {
            return new CallToolResponse(Collections.singletonList(new Content("Error: name is required")));
        }
        
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
        if (suite == null) {
            return new CallToolResponse(Collections.singletonList(new Content("Error: suite is required")));
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("./gradlew", "test", "-Dsuite=" + suite);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append('\n');
                }
            }
            
            int exitCode = process.waitFor();
            String result = exitCode == 0 ? "SUCCESS" : "FAILED";
            String msg = String.format("Suite %s finished with %s\n\nOutput:\n%s", suite, result, output.toString());
            
            return new CallToolResponse(Collections.singletonList(new Content(msg)));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CallToolResponse(Collections.singletonList(new Content("Error running suite (interrupted): " + e.getMessage())));
        } catch (IOException e) {
            return new CallToolResponse(Collections.singletonList(new Content("Error running suite: " + e.getMessage())));
        }
    }

    @SuppressWarnings("PMD.SystemPrintln")
    private void sendResponse(Object id, Object result) throws IOException {
        Response response = new Response(id, result);
        System.out.println(mapper.writeValueAsString(response));
        System.out.flush();
    }

    @SuppressWarnings("PMD.SystemPrintln")
    private void sendError(Object id, int code, String message) throws IOException {
        Response response = new Response();
        response.id = id;
        response.error = new McpProtocol.Error(code, message);
        System.out.println(mapper.writeValueAsString(response));
        System.out.flush();
    }

    @FunctionalInterface
    interface ToolHandler {
        CallToolResponse handle(Map<String, Object> arguments) throws IOException;
    }
}
