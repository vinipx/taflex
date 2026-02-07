package io.github.vinipx.taflex.core.drivers.strategies;

import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.drivers.AutomationDriver;
import io.github.vinipx.taflex.core.drivers.elements.Element;
import io.github.vinipx.taflex.core.drivers.elements.ApiElement;
import io.github.vinipx.taflex.core.exceptions.DriverException;
import io.github.vinipx.taflex.core.locators.LocatorFactory;
import io.github.vinipx.taflex.core.locators.LocatorStrategy;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * API automation driver implementation using Apache HttpClient.
 * Supports REST, SOAP, and gRPC (via HTTP/2).
 */
public class ApiDriverStrategy implements AutomationDriver {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiDriverStrategy.class);
    
    private CloseableHttpClient httpClient;
    private LocatorStrategy locatorStrategy;
    private String baseUrl;
    private Map<String, String> defaultHeaders;
    private HttpResponse lastResponse;
    
    @Override
    public void initialize() {
        logger.info("Initializing API driver (HttpClient)");
        
        try {
            // Initialize HttpClient
            httpClient = HttpClients.createDefault();
            
            // Get configuration
            baseUrl = ConfigManager.getApiBaseUrl();
            if (baseUrl == null || baseUrl.isEmpty()) {
                throw new DriverException("api.base.url not configured in automation.properties");
            }
            
            // Initialize default headers
            defaultHeaders = new HashMap<>();
            defaultHeaders.put("Content-Type", ConfigManager.getProperty("api.content.type", "application/json"));
            defaultHeaders.put("Accept", "application/json");
            
            // Initialize locator strategy
            locatorStrategy = LocatorFactory.getLocatorStrategy();
            
            logger.info("API driver initialized successfully. Base URL: {}", baseUrl);
            
        } catch (Exception e) {
            logger.error("Failed to initialize API driver", e);
            throw new DriverException("Failed to initialize API driver", e);
        }
    }
    
    @Override
    public void terminate() {
        logger.info("Terminating API driver");
        
        try {
            if (httpClient != null) {
                httpClient.close();
            }
            logger.info("API driver terminated successfully");
        } catch (IOException e) {
            logger.error("Error terminating API driver", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getNativeDriver() {
        return (T) httpClient;
    }
    
    @Override
    public void navigateTo(String urlKey) {
        // Not applicable for API tests, but could be used for health checks
        logger.warn("navigateTo() is not applicable for API driver");
    }
    
    @Override
    public Element findElement(String logicalName) {
        // For API, this returns the response body or a specific field
        if (lastResponse == null) {
            throw new DriverException("No API response available. Execute a request first.");
        }
        return new ApiElement(lastResponse, logicalName);
    }
    
    @Override
    public void click(String logicalName) {
        throw new UnsupportedOperationException("click() is not supported for API driver");
    }
    
    @Override
    public void type(String logicalName, String text) {
        throw new UnsupportedOperationException("type() is not supported for API driver");
    }
    
    @Override
    public String getText(String logicalName) {
        return findElement(logicalName).getText();
    }
    
    @Override
    public boolean isVisible(String logicalName) {
        // For API, checks if field exists in response
        return findElement(logicalName).isVisible();
    }
    
    @Override
    public void waitForVisible(String logicalName, int timeoutSeconds) {
        // Not applicable for API
        logger.warn("waitForVisible() is not applicable for API driver");
    }
    
    @Override
    public String captureScreenshot(String fileName) {
        // Not applicable for API
        logger.warn("captureScreenshot() is not applicable for API driver");
        return null;
    }
    
    @Override
    public String getExecutionMode() {
        return "api";
    }
    
    /**
     * Execute GET request
     * @param endpointKey Locator key for the endpoint path
     * @return ApiResponse
     */
    public ApiResponse get(String endpointKey) {
        return get(endpointKey, null);
    }
    
    /**
     * Execute GET request with headers
     * @param endpointKey Locator key for the endpoint path
     * @param headers Additional headers
     * @return ApiResponse
     */
    public ApiResponse get(String endpointKey, Map<String, String> headers) {
        String endpoint = locatorStrategy.resolve(endpointKey);
        String url = baseUrl + endpoint;
        
        logger.info("GET {}", url);
        
        HttpGet request = new HttpGet(url);
        addHeaders(request, headers);
        
        return executeRequest(request);
    }
    
    /**
     * Execute POST request
     * @param endpointKey Locator key for the endpoint path
     * @param body Request body (JSON string)
     * @return ApiResponse
     */
    public ApiResponse post(String endpointKey, String body) {
        return post(endpointKey, body, null);
    }
    
    /**
     * Execute POST request with headers
     * @param endpointKey Locator key for the endpoint path
     * @param body Request body
     * @param headers Additional headers
     * @return ApiResponse
     */
    public ApiResponse post(String endpointKey, String body, Map<String, String> headers) {
        String endpoint = locatorStrategy.resolve(endpointKey);
        String url = baseUrl + endpoint;
        
        logger.info("POST {}", url);
        
        HttpPost request = new HttpPost(url);
        addHeaders(request, headers);
        
        if (body != null && !body.isEmpty()) {
            request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        }
        
        return executeRequest(request);
    }
    
    /**
     * Execute PUT request
     * @param endpointKey Locator key for the endpoint path
     * @param body Request body
     * @return ApiResponse
     */
    public ApiResponse put(String endpointKey, String body) {
        String endpoint = locatorStrategy.resolve(endpointKey);
        String url = baseUrl + endpoint;
        
        logger.info("PUT {}", url);
        
        HttpPut request = new HttpPut(url);
        request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        
        return executeRequest(request);
    }
    
    /**
     * Execute DELETE request
     * @param endpointKey Locator key for the endpoint path
     * @return ApiResponse
     */
    public ApiResponse delete(String endpointKey) {
        String endpoint = locatorStrategy.resolve(endpointKey);
        String url = baseUrl + endpoint;
        
        logger.info("DELETE {}", url);
        
        HttpDelete request = new HttpDelete(url);
        return executeRequest(request);
    }
    
    /**
     * Execute PATCH request
     * @param endpointKey Locator key for the endpoint path
     * @param body Request body
     * @return ApiResponse
     */
    public ApiResponse patch(String endpointKey, String body) {
        String endpoint = locatorStrategy.resolve(endpointKey);
        String url = baseUrl + endpoint;
        
        logger.info("PATCH {}", url);
        
        HttpPatch request = new HttpPatch(url);
        request.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
        
        return executeRequest(request);
    }
    
    /**
     * Execute any HttpUriRequest
     * @param request The request to execute
     * @return ApiResponse
     */
    private ApiResponse executeRequest(HttpUriRequest request) {
        try {
            lastResponse = httpClient.execute(request);
            ApiResponse response = new ApiResponse(lastResponse);
            logger.info("Response status: {}", response.getStatusCode());
            return response;
        } catch (IOException e) {
            logger.error("Failed to execute request", e);
            throw new DriverException("Failed to execute API request", e);
        }
    }
    
    private void addHeaders(HttpUriRequest request, Map<String, String> additionalHeaders) {
        // Add default headers
        defaultHeaders.forEach(request::addHeader);
        
        // Add additional headers
        if (additionalHeaders != null) {
            additionalHeaders.forEach(request::addHeader);
        }
    }
    
    /**
     * Get the last response received
     * @return Last HttpResponse
     */
    public HttpResponse getLastResponse() {
        return lastResponse;
    }
    
    /**
     * Set default header for all requests
     * @param name Header name
     * @param value Header value
     */
    public void setDefaultHeader(String name, String value) {
        defaultHeaders.put(name, value);
    }
    
    /**
     * API Response wrapper class
     */
    public static class ApiResponse {
        private final int statusCode;
        private final String body;
        private final Map<String, String> headers;
        
        public ApiResponse(HttpResponse response) {
            this.statusCode = response.getStatusLine().getStatusCode();
            this.headers = new HashMap<>();
            
            // Extract headers
            for (Header header : response.getAllHeaders()) {
                headers.put(header.getName(), header.getValue());
            }
            
            // Extract body
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    this.body = EntityUtils.toString(entity);
                } catch (IOException e) {
                    throw new DriverException("Failed to read response body", e);
                }
            } else {
                this.body = null;
            }
        }
        
        public int getStatusCode() {
            return statusCode;
        }
        
        public String getBody() {
            return body;
        }
        
        public Map<String, String> getHeaders() {
            return new HashMap<>(headers);
        }
        
        public String getHeader(String name) {
            return headers.get(name);
        }
        
        public boolean isSuccessful() {
            return statusCode >= 200 && statusCode < 300;
        }
    }
}