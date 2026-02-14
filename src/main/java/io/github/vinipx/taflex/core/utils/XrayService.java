package io.github.vinipx.taflex.core.utils;

import io.github.vinipx.taflex.core.config.ConfigManager;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for interacting with the Xray Cloud API.
 *
 * <p>Provides authentication and results-import capabilities to link automated
 * test executions with Jira/Xray issues.
 */
public class XrayService {
    
    private static final Logger logger = LoggerFactory.getLogger(XrayService.class);
    private static final String BASE_URL = "https://xray.cloud.getxray.app/api/v2";
    
    private String token;

    /**
     * Authenticates with Xray Cloud using configured Client ID and Secret.
     *
     * <p>The token is stored internally for subsequent API calls in the same session.
     */
    public void authenticate() {
        String clientId = ConfigManager.getProperty("xray.client.id");
        String clientSecret = ConfigManager.getProperty("xray.client.secret");

        if (clientId == null || clientSecret == null) {
            logger.warn("Xray credentials not configured. Skipping authentication.");
            return;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/authenticate");
            String json = String.format("{\"client_id\":\"%s\",\"client_secret\":\"%s\"}", clientId, clientSecret);
            request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    // Simple token extraction (should use a proper JSON parser in production)
                    this.token = new java.util.Scanner(response.getEntity().getContent()).useDelimiter("\\A").next().replace("\"", "");
                    logger.info("Authenticated with Xray successfully");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to authenticate with Xray", e);
        }
    }

    /**
     * Imports automated test execution results into Xray.
     *
     * @param resultsJson The execution results in Xray-compatible JSON format.
     */
    public void importExecution(String resultsJson) {
        if (token == null) {
            authenticate();
        }
        if (token == null) {
            return;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/import/execution");
            request.setHeader("Authorization", "Bearer " + token);
            request.setEntity(new StringEntity(resultsJson, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                logger.info("Imported results to Xray. Status: {}", response.getCode());
            }
        } catch (Exception e) {
            logger.error("Failed to import execution to Xray", e);
        }
    }
}
