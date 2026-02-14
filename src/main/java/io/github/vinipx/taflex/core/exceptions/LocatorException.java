package io.github.vinipx.taflex.core.exceptions;

/**
 * Exception for locator-related errors
 */
public class LocatorException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public LocatorException(String message) {
        super(message);
    }
    
    public LocatorException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public LocatorException(String locatorName, String reason) {
        super(String.format("Locator '%s' error: %s", locatorName, reason));
    }
}