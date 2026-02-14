package io.github.vinipx.taflex.core.drivers.elements;

/**
 * Unified interface for UI and API elements within the TAFLEX framework.
 *
 * <p>This interface abstracts common interactions such as clicking, typing,
 * and checking visibility. Implementations handle the specifics for different
 * platforms (Playwright, Appium, HttpClient).
 */
public interface Element {
    
    /**
     * Executes a click action on the element.
     */
    void click();
    
    /**
     * Types the provided text into the element.
     *
     * @param text The string to be typed.
     */
    void type(String text);
    
    /**
     * Removes all existing content/text from the element.
     */
    void clear();
    
    /**
     * Retrieves the visible text or value content of the element.
     *
     * @return The element's text content.
     */
    String getText();
    
    /**
     * Checks if the element is currently visible to the user.
     *
     * @return true if visible, false otherwise.
     */
    boolean isVisible();
    
    /**
     * Checks if the element is currently enabled for interaction.
     *
     * @return true if enabled, false otherwise.
     */
    boolean isEnabled();
    
    /**
     * Checks if the element is currently selected (e.g., checkbox, radio button).
     *
     * @return true if selected, false otherwise.
     */
    boolean isSelected();
    
    /**
     * Retrieves the value of a specific attribute of the element.
     *
     * @param attributeName The name of the attribute (e.g., "id", "class", "href").
     * @return The attribute value string.
     */
    String getAttribute(String attributeName);
    
    /**
     * Blocks execution until the element becomes visible or the timeout expires.
     *
     * @param timeoutSeconds Maximum duration to wait in seconds.
     */
    void waitForVisible(int timeoutSeconds);
    
    /**
     * Blocks execution until the element is both visible and enabled.
     *
     * @param timeoutSeconds Maximum duration to wait in seconds.
     */
    void waitForClickable(int timeoutSeconds);
    
    /**
     * Provides access to the underlying platform-specific element object.
     *
     * @param <T> The expected native type.
     * @return The native element (e.g., {@code com.microsoft.playwright.Locator}).
     */
    <T> T getNativeElement();
    
    /**
     * Ensures the element is scrolled into the viewport.
     */
    void scrollIntoView();
    
    /**
     * Performs a mouse hover action over the element.
     */
    void hover();
}
