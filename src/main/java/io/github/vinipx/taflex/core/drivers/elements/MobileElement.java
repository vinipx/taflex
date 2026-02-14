package io.github.vinipx.taflex.core.drivers.elements;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mobile-specific implementation of Element interface using Appium WebElement.
 */
public class MobileElement implements Element {
    
    private static final Logger logger = LoggerFactory.getLogger(MobileElement.class);
    
    private final WebElement element;
    private final String logicalName;
    
    public MobileElement(WebElement element, String logicalName) {
        this.element = element;
        this.logicalName = logicalName;
    }
    
    @Override
    public void click() {
        logger.debug("Clicking element: {}", logicalName);
        element.click();
    }
    
    @Override
    public void type(String text) {
        logger.debug("Typing '{}' into element: {}", text, logicalName);
        element.sendKeys(text);
    }
    
    @Override
    public void clear() {
        logger.debug("Clearing element: {}", logicalName);
        element.clear();
    }
    
    @Override
    public String getText() {
        String text = element.getText();
        logger.debug("Got text from element {}: {}", logicalName, text);
        return text;
    }
    
    @Override
    public boolean isVisible() {
        boolean displayed = element.isDisplayed();
        logger.debug("Element {} displayed: {}", logicalName, displayed);
        return displayed;
    }
    
    @Override
    public boolean isEnabled() {
        boolean enabled = element.isEnabled();
        logger.debug("Element {} enabled: {}", logicalName, enabled);
        return enabled;
    }
    
    @Override
    public boolean isSelected() {
        boolean selected = element.isSelected();
        logger.debug("Element {} selected: {}", logicalName, selected);
        return selected;
    }
    
    @Override
    public String getAttribute(String attributeName) {
        String value = element.getAttribute(attributeName);
        logger.debug("Got attribute '{}' from element {}: {}", attributeName, logicalName, value);
        return value;
    }
    
    @Override
    public void waitForVisible(int timeoutSeconds) {
        logger.debug("Waiting for element {} to be visible (timeout: {}s)", logicalName, timeoutSeconds);
        // Appium doesn't have explicit wait methods on WebElement
        // This would typically use WebDriverWait
        // For simplicity, we'll check visibility
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000);
        while (System.currentTimeMillis() < endTime) {
            if (element.isDisplayed()) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        throw new RuntimeException("Element not visible within timeout: " + logicalName);
    }
    
    @Override
    public void waitForClickable(int timeoutSeconds) {
        logger.debug("Waiting for element {} to be clickable (timeout: {}s)", logicalName, timeoutSeconds);
        // Similar to waitForVisible, but also check enabled
        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000);
        while (System.currentTimeMillis() < endTime) {
            if (element.isDisplayed() && element.isEnabled()) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        throw new RuntimeException("Element not clickable within timeout: " + logicalName);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getNativeElement() {
        return (T) element;
    }
    
    @Override
    public void scrollIntoView() {
        // For mobile, this might need platform-specific implementation
        logger.debug("Scrolling element {} into view", logicalName);
        // Implementation depends on platform
    }
    
    @Override
    public void hover() {
        // Hover is not typically used in mobile
        logger.warn("Hover is not applicable for mobile elements");
    }
    
    /**
     * Get the underlying WebElement
     * @return WebElement instance
     */
    public WebElement getWebElement() {
        return element;
    }
    
    /**
     * Tap on the element (mobile-specific)
     */
    public void tap() {
        logger.debug("Tapping element: {}", logicalName);
        element.click();
    }
    
    /**
     * Long press on the element
     */
    public void longPress() {
        logger.debug("Long pressing element: {}", logicalName);
        // Implementation would use TouchAction or W3C Actions
    }
}