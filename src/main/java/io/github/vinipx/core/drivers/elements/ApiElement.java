package io.github.vinipx.taflex.core.drivers.elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * API-specific element wrapper for response bodies.
 * Allows accessing JSON fields as elements.
 */
public class ApiElement implements Element {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiElement.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final HttpResponse response;
    private final String logicalName;
    private final JsonNode jsonBody;
    
    public ApiElement(HttpResponse response, String logicalName) {
        this.response = response;
        this.logicalName = logicalName;
        this.jsonBody = parseBody();
    }
    
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
     * Get value from JSON using dot notation (e.g., "user.name")
     * @param path Dot-notation path to the field
     * @return Field value as string
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
    
    @Override
    public String getText() {
        // If logicalName contains a dot, treat it as JSON path
        if (logicalName.contains(".")) {
            return getJsonValue(logicalName);
        }
        
        // Otherwise return entire body
        try {
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            logger.error("Failed to get response body", e);
            return null;
        }
    }
    
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
        // Not applicable for API
        return true;
    }
    
    @Override
    public boolean isSelected() {
        // Not applicable for API
        return false;
    }
    
    @Override
    public String getAttribute(String attributeName) {
        // For API, could return headers or other metadata
        if (attributeName.equalsIgnoreCase("statusCode")) {
            return String.valueOf(response.getStatusLine().getStatusCode());
        }
        return null;
    }
    
    @Override
    public void waitForVisible(int timeoutSeconds) {
        // Not applicable for API
        logger.warn("waitForVisible() is not applicable for API elements");
    }
    
    @Override
    public void waitForClickable(int timeoutSeconds) {
        // Not applicable for API
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
     * Get the HTTP status code
     * @return Status code
     */
    public int getStatusCode() {
        return response.getStatusLine().getStatusCode();
    }
    
    /**
     * Get the full JSON body
     * @return JsonNode
     */
    public JsonNode getJsonBody() {
        return jsonBody;
    }
    
    /**
     * Check if response is successful (2xx)
     * @return true if successful
     */
    public boolean isSuccessful() {
        int code = getStatusCode();
        return code >= 200 && code < 300;
    }
}