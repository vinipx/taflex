package io.github.vinipx.taflex.core.drivers.elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * API-specific implementation of the {@code Element} interface.
 *
 * <p>This class wraps an {@code HttpResponse} and allows treating parts of the JSON
 * response body as interactable elements. It supports hierarchical field access
 * using dot-notation (e.g., "user.profile.id").
 */
public class ApiElement implements Element {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiElement.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final HttpResponse response;
    private final String logicalName;
    private final JsonNode jsonBody;
    
    /**
     * Constructs an ApiElement from an HTTP response.
     *
     * @param response    The Apache HttpClient response object.
     * @param logicalName The name of the field to interact with, or dot-notation path.
     */
    public ApiElement(HttpResponse response, String logicalName) {
        this.response = response;
        this.logicalName = logicalName;
        this.jsonBody = parseBody();
    }
    
    /**
     * Parses the response entity into a Jackson JsonNode for efficient field access.
     *
     * @return The root JsonNode, or null if the body is empty or unparsable.
     */
    private JsonNode parseBody() {
        try {
            String body = EntityUtils.toString(response.getEntity());
            if (body != null && !body.isEmpty()) {
                return objectMapper.readTree(body);
            }
            return null;
        } catch (IOException e) {
            logger.error("Failed to parse response body", e);
            return null;
        }
    }
    
    /**
     * Navigates the JSON tree using a dot-notation path.
     *
     * @param path The path to the desired field (e.g., "data.items[0].name").
     * @return The field value as a string, or null if the path is invalid.
     */
    public String getJsonValue(String path) {
        if (jsonBody == null) {
            return null;
        }
        
        String[] parts = path.split("\\.");
        JsonNode current = jsonBody;
        
        for (String part : parts) {
            if (current.has(part)) {
                current = current.get(part);
            } else {
                return null;
            }
        }
        
        return current.asText();
    }
    
    @Override
    public void click() {
        throw new UnsupportedOperationException("click() is not supported for API elements");
    }
    
    @Override
    public void type(String text) {
        throw new UnsupportedOperationException("type() is not supported for API elements");
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear() is not supported for API elements");
    }
    
    /**
     * Returns either a specific field value (if logicalName is a path) or the entire body.
     *
     * @return The text representation of the response data.
     */
    @Override
    public String getText() {
        // If logicalName contains a dot, treat it as JSON path
        if (logicalName.contains(".")) {
            return getJsonValue(logicalName);
        }
        
        // Otherwise return entire body
        try {
            String body = EntityUtils.toString(response.getEntity());
            return (body != null) ? body : "";
        } catch (IOException e) {
            logger.error("Failed to get response body", e);
            return null;
        }
    }
    
    /**
     * Checks if the logical name exists as a key in the JSON response.
     *
     * @return true if the field exists, false otherwise.
     */
    @Override
    public boolean isVisible() {
        // For API, checks if the field exists in response
        if (logicalName.contains(".")) {
            return getJsonValue(logicalName) != null;
        }
        return jsonBody != null && jsonBody.has(logicalName);
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    @Override
    public boolean isSelected() {
        return false;
    }
    
    /**
     * Retrieves metadata from the API response.
     * Supports "statusCode" as a special attribute.
     *
     * @param attributeName The name of the metadata to retrieve.
     * @return The attribute value or null.
     */
    @Override
    public String getAttribute(String attributeName) {
        if ("statusCode".equalsIgnoreCase(attributeName)) {
            return String.valueOf(response.getStatusLine().getStatusCode());
        }
        return null;
    }
    
    @Override
    public void waitForVisible(int timeoutSeconds) {
        logger.warn("waitForVisible() is not applicable for API elements");
    }
    
    @Override
    public void waitForClickable(int timeoutSeconds) {
        logger.warn("waitForClickable() is not applicable for API elements");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getNativeElement() {
        return (T) response;
    }
    
    @Override
    public void scrollIntoView() {
        throw new UnsupportedOperationException("scrollIntoView() is not supported for API elements");
    }
    
    @Override
    public void hover() {
        throw new UnsupportedOperationException("hover() is not supported for API elements");
    }
    
    /**
     * Retrieves the HTTP status code of the response.
     *
     * @return The status code (e.g., 200, 404).
     */
    public int getStatusCode() {
        return response.getStatusLine().getStatusCode();
    }
    
    /**
     * Returns the root JsonNode of the response body.
     *
     * @return The parsed JSON body.
     */
    public JsonNode getJsonBody() {
        return jsonBody;
    }
    
    /**
     * Helper to verify if the status code indicates a successful request.
     *
     * @return true if code is in the 200-299 range.
     */
    public boolean isSuccessful() {
        int code = getStatusCode();
        return code >= 200 && code < 300;
    }
}
