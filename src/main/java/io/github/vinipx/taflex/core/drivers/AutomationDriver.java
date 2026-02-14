package io.github.vinipx.taflex.core.drivers;

import io.github.vinipx.taflex.core.drivers.elements.Element;

/**
 * Core interface for all TAFLEX automation drivers.
 *
 * <p>This interface defines a unified API that abstracts the underlying tool (Playwright,
 * HttpClient, or Appium). By interacting with this interface, tests can remain
 * largely independent of the specific automation engine being used.
 */
public interface AutomationDriver {
    
    /**
     * Initializes the underlying automation engine.
     *
     * <p>This method should read configuration from the {@code ConfigManager} and
     * set up the necessary environment (e.g., launch browser, start HTTP session).
     */
    void initialize();
    
    /**
     * Performs a clean shutdown of the automation driver.
     *
     * <p>This should release all resources, close browser windows, or terminate
     * active processes.
     */
    void terminate();
    
    /**
     * Retrieves the native tool instance for advanced scenarios.
     *
     * @param <T> The expected type of the native driver.
     * @return The raw driver instance (e.g., {@code com.microsoft.playwright.Page}).
     */
    <T> T getNativeDriver();
    
    /**
     * Navigates the current session to a specific URL.
     *
     * @param urlKey The logical key in the locator properties file mapped to the target URL.
     */
    void navigateTo(String urlKey);
    
    /**
     * Finds a single element using an externalized logical locator name.
     *
     * @param logicalName The logical name of the locator defined in properties/JSON files.
     * @return A wrapped {@code Element} instance for interaction.
     */
    Element findElement(String logicalName);
    
    /**
     * Executes a click action on the element identified by the logical name.
     *
     * @param logicalName The logical name of the locator.
     */
    void click(String logicalName);
    
    /**
     * Clears and types text into the specified element.
     *
     * @param logicalName The logical name of the locator.
     * @param text        The character sequence to type.
     */
    void type(String logicalName, String text);
    
    /**
     * Retrieves the text content of the specified element.
     *
     * @param logicalName The logical name of the locator.
     * @return The visible text content of the element.
     */
    String getText(String logicalName);
    
    /**
     * Checks if the specified element is currently visible in the UI.
     *
     * @param logicalName The logical name of the locator.
     * @return true if visible, false otherwise.
     */
    boolean isVisible(String logicalName);
    
    /**
     * Halts execution until the specified element becomes visible or the timeout is reached.
     *
     * @param logicalName    The logical name of the locator.
     * @param timeoutSeconds The maximum time to wait in seconds.
     */
    void waitForVisible(String logicalName, int timeoutSeconds);
    
    /**
     * Captures a screenshot of the current state and saves it to the filesystem.
     *
     * @param fileName The base name for the screenshot file (without extension).
     * @return The absolute or relative path to the saved screenshot file.
     */
    String captureScreenshot(String fileName);
    
    /**
     * Identifies the current active execution mode of this driver.
     *
     * @return A string representing the mode ("web", "api", "mobile").
     */
    String getExecutionMode();
}
