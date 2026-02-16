package io.github.vinipx.taflex.mcp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Basic models for the Model Context Protocol (MCP) JSON-RPC communication.
 */
public class McpProtocol {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Request {
        public String jsonrpc = "2.0";
        public Object id;
        public String method;
        public Map<String, Object> params;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        public String jsonrpc = "2.0";
        public Object id;
        public Object result;
        public Error error;

        public Response() {}
        public Response(Object id, Object result) {
            this.id = id;
            this.result = result;
        }
    }

    public static class Error {
        public int code;
        public String message;
        public Object data;

        public Error() {}
        public Error(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    public static class Notification {
        public String jsonrpc = "2.0";
        public String method;
        public Map<String, Object> params;
    }

    public static class Tool {
        public String name;
        public String description;
        public JsonNode inputSchema;

        public Tool() {}
        public Tool(String name, String description, JsonNode inputSchema) {
            this.name = name;
            this.description = description;
            this.inputSchema = inputSchema;
        }
    }

    public static class ToolListResponse {
        public List<Tool> tools;

        public ToolListResponse(List<Tool> tools) {
            this.tools = tools;
        }
    }

    public static class CallToolResponse {
        public List<Content> content;
        public boolean isError;

        public CallToolResponse(List<Content> content) {
            this.content = content;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Content {
        public String type = "text";
        public String text;

        public Content() {}
        public Content(String text) {
            this.text = text;
        }
    }
}
