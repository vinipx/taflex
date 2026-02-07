package com.enterprise.taflex.core.drivers.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Playwright-specific implementation of Element interface.
 * Wraps Playwright Locator to provide unified API.
 */
public class PlaywrightElement implements Element {
    
    private static final Logger logger = LoggerFactory.getLogger(PlaywrightElement.class);
    
    private final Locator locator;
    private final String logicalName;
    
    public PlaywrightElement(Locator locator, String logicalName) {
        this.locator = locator;
        this.logicalName = logicalName;
    }
    
    @Override
    public void click() {
        logger.debug("Clicking element: {}", logicalName);
        locator.click();
    }
    
    @Override
    public void type(String text) {
        logger.debug("Typing '{}' into element: {}", text, logicalName);
        locator.fill(text);
    }
    
    @Override
    public void clear() {
        logger.debug("Clearing element: {}", logicalName);
        locator.clear();
    }
    
    @Override
    public String getText() {
        String text = locator.textContent();
        logger.debug("Got text from element {}: {}", logicalName, text);
        return text;
    }
    
    @Override
    public boolean isVisible() {
        boolean visible = locator.isVisible();
        logger.debug("Element {} visible: {}", logicalName, visible);
        return visible;
    }
    
    @Override
    public boolean isEnabled() {
        boolean enabled = locator.isEnabled();
        logger.debug("Element {} enabled: {}", logicalName, enabled);
        return enabled;
    }
    
    @Override
    public boolean isSelected() {
        boolean checked = locator.isChecked();
        logger.debug("Element {} checked: {}", logicalName, checked);
        return checked;
    }
    
    @Override
    public String getAttribute(String attributeName) {
        String value = locator.getAttribute(attributeName);
        logger.debug("Got attribute '{}' from element {}: {}", attributeName, logicalName, value);
        return value;
    }
    
    @Override
    public void waitForVisible(int timeoutSeconds) {
        logger.debug("Waiting for element {} to be visible (timeout: {}s)", logicalName, timeoutSeconds);
        locator.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(timeoutSeconds * 1000));
    }
    
    @Override
    public void waitForClickable(int timeoutSeconds) {
        logger.debug("Waiting for element {} to be clickable (timeout: {}s)", logicalName, timeoutSeconds);
        locator.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(timeoutSeconds * 1000));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getNativeElement() {
        return (T) locator;
    }
    
    @Override
    public void scrollIntoView() {
        logger.debug("Scrolling element {} into view", logicalName);
        locator.scrollIntoViewIfNeeded();
    }
    
    @Override
    public void hover() {
        logger.debug("Hovering over element {}", logicalName);
        locator.hover();
    }
    
    /**
     * Get the underlying Playwright Locator
     * @return Locator instance
     */
    public Locator getLocator() {
        return locator;
    }
    
    /**
     * Get the logical name of this element
     * @return Logical name
     */
    public String getLogicalName() {
        return logicalName;
    }
}