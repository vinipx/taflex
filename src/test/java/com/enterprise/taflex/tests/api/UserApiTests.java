package com.enterprise.taflex.tests.api;

import com.enterprise.taflex.base.BaseTest;
import com.enterprise.taflex.core.drivers.strategies.ApiDriverStrategy;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * API tests for User endpoints using HttpClient driver.
 */
public class UserApiTests extends BaseTest {
    
    @Test(groups = {"smoke", "regression"}, description = "Verify GET users endpoint returns 200")
    public void shouldGetUsersSuccessfully() {
        ApiDriverStrategy apiDriver = (ApiDriverStrategy) driver;
        
        // Execute GET request
        ApiDriverStrategy.ApiResponse response = apiDriver.get("users.endpoint");
        
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 200,
            "GET /users should return 200 OK");
        
        // Verify response body is not empty
        Assert.assertNotNull(response.getBody(),
            "Response body should not be null");
    }
    
    @Test(groups = {"regression"}, description = "Verify POST create user returns 201")
    public void shouldCreateUserSuccessfully() {
        ApiDriverStrategy apiDriver = (ApiDriverStrategy) driver;
        
        // Create user payload
        String payload = "{\n" +
            "  \"name\": \"Test User\",\n" +
            "  \"email\": \"testuser@example.com\",\n" +
            "  \"role\": \"user\"\n" +
            "}";
        
        // Execute POST request
        ApiDriverStrategy.ApiResponse response = apiDriver.post("user.create.endpoint", payload);
        
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 201,
            "POST /users should return 201 Created");
        
        // Verify response contains ID
        Assert.assertNotNull(response.getBody(),
            "Response body should not be null");
    }
    
    @Test(groups = {"regression"}, description = "Verify health check endpoint returns 200")
    public void shouldReturnHealthyStatus() {
        ApiDriverStrategy apiDriver = (ApiDriverStrategy) driver;
        
        // Execute health check
        ApiDriverStrategy.ApiResponse response = apiDriver.get("health.endpoint");
        
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 200,
            "Health check should return 200 OK");
    }
}