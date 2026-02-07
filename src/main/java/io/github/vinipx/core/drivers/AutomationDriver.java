package io.github.vinipx.taflex.core.drivers;

import io.github.vinipx.taflex.core.drivers.elements.Element;

/**
 * Core interface for all automation drivers.
 * Provides a unified API for Web, API, and Mobile automation.
 */
public interface AutomationDriver {
    
    /**
     * Initialize the driver with configuration from automation.properties
     */
    void initialize();
    
    /**
     * Clean up and terminate the driver
     */
    void terminate();
    
    /**
     * Get the native driver implementation
     * @return Native driver (Page for Playwright, CloseableHttpClient for API, AppiumDriver for Mobile)
     */
    <T> T getNativeDriver();
    
    /**
     * Navigate to a URL (for Web/Mobile)
     * @param urlKey Key from locators file containing the URL
     */
    void navigateTo(String urlKey);
    
    /**
     * Find an element using externalized locator
     * @param logicalName Logical name of the locator from properties file
     * @return Element wrapper
     */
    Element findElement(String logicalName);
    
    /**
     * Click on an element
     * @param logicalName Logical name of the locator from properties file
     */
    void click(String logicalName);
    
    /**
     * Type text into an element
     * @param logicalName Logical name of the locator from properties file
     * @param text Text to type
     */
    void type(String logicalName, String text);
    
    /**
     * Get text from an element
     * @param logicalName Logical name of the locator from properties file
     * @return Text content
     */
    String getText(String logicalName);
    
    /**
     * Check if element is visible
     * @param logicalName Logical name of the locator from properties file
     * @return true if visible
     */
    boolean isVisible(String logicalName);
    
    /**
     * Wait for element to be visible
     * @param logicalName Logical name of the locator from properties file
     * @param timeoutSeconds Timeout in seconds
     */
    void waitForVisible(String logicalName, int timeoutSeconds);
    
    /**
     * Capture screenshot
     * @param fileName Name for the screenshot file
     * @return Path to saved screenshot
     */
    String captureScreenshot(String fileName);
    
    /**
     * Get the execution mode (web, api, mobile)
     * @return Execution mode string
     */
    String getExecutionMode();
}