package io.github.vinipx.taflex.core.drivers.elements;

/**
 * Unified element interface for Web and Mobile elements.
 * Provides common operations regardless of underlying driver.
 */
public interface Element {
    
    /**
     * Click on the element
     */
    void click();
    
    /**
     * Type text into the element
     * @param text Text to type
     */
    void type(String text);
    
    /**
     * Clear the element's content
     */
    void clear();
    
    /**
     * Get text content of the element
     * @return Text content
     */
    String getText();
    
    /**
     * Check if element is visible
     * @return true if visible
     */
    boolean isVisible();
    
    /**
     * Check if element is enabled
     * @return true if enabled
     */
    boolean isEnabled();
    
    /**
     * Check if element is selected (checkboxes, radio buttons)
     * @return true if selected
     */
    boolean isSelected();
    
    /**
     * Get attribute value
     * @param attributeName Name of the attribute
     * @return Attribute value
     */
    String getAttribute(String attributeName);
    
    /**
     * Wait for element to be visible
     * @param timeoutSeconds Timeout in seconds
     */
    void waitForVisible(int timeoutSeconds);
    
    /**
     * Wait for element to be clickable
     * @param timeoutSeconds Timeout in seconds
     */
    void waitForClickable(int timeoutSeconds);
    
    /**
     * Get the underlying native element
     * @return Native element (Locator for Playwright, WebElement for Selenium/Appium)
     */
    <T> T getNativeElement();
    
    /**
     * Scroll element into view
     */
    void scrollIntoView();
    
    /**
     * Hover over element
     */
    void hover();
}