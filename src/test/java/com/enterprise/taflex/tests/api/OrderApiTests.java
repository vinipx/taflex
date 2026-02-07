package com.enterprise.taflex.tests.api;

import com.enterprise.taflex.base.BaseTest;
import com.enterprise.taflex.core.drivers.strategies.ApiDriverStrategy;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * API tests for Order endpoints.
 */
public class OrderApiTests extends BaseTest {
    
    @Test(groups = {"smoke", "regression"}, description = "Verify GET orders endpoint returns 200")
    public void shouldGetOrdersSuccessfully() {
        ApiDriverStrategy apiDriver = (ApiDriverStrategy) driver;
        
        // Execute GET request
        ApiDriverStrategy.ApiResponse response = apiDriver.get("orders.endpoint");
        
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 200,
            "GET /orders should return 200 OK");
    }
    
    @Test(groups = {"regression"}, description = "Verify POST create order returns 201")
    public void shouldCreateOrderSuccessfully() {
        ApiDriverStrategy apiDriver = (ApiDriverStrategy) driver;
        
        // Create order payload
        String payload = "{\n" +
            "  \"productId\": \"12345\",\n" +
            "  \"quantity\": 2,\n" +
            "  \"customerId\": \"cust-001\"\n" +
            "}";
        
        // Execute POST request
        ApiDriverStrategy.ApiResponse response = apiDriver.post("order.create.endpoint", payload);
        
        // Verify status code
        Assert.assertEquals(response.getStatusCode(), 201,
            "POST /orders should return 201 Created");
    }
    
    @Test(groups = {"regression"}, description = "Verify DELETE order returns 204")
    public void shouldDeleteOrderSuccessfully() {
        ApiDriverStrategy apiDriver = (ApiDriverStrategy) driver;
        
        // Note: In real tests, you'd create an order first, then delete it
        // This is a simplified example
        String orderId = "test-order-123";
        
        // Execute DELETE request
        // This assumes the endpoint supports /orders/{id}
        ApiDriverStrategy.ApiResponse response = apiDriver.delete("orders.endpoint");
        
        // Verify status code (200 or 204)
        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 204,
            "DELETE /orders should return 200 or 204");
    }
}