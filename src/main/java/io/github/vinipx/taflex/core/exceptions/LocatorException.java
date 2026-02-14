package io.github.vinipx.taflex.core.exceptions;

/**
 * Specialized exception for errors encountered during locator resolution and loading.
 *
 * <p>Thrown when a logical locator name cannot be found in properties/JSON files,
 * or when a locator source file is missing or malformed.
 */
public class LocatorException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a LocatorException with a generic message.
     *
     * @param message The error message.
     */
    public LocatorException(String message) {
        super(message);
    }
    
    /**
     * Constructs a LocatorException with an underlying cause.
     *
     * @param message The error message.
     * @param cause   The root cause exception.
     */
    public LocatorException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a detailed LocatorException for a specific missing locator.
     *
     * @param locatorName The name of the locator that failed to resolve.
     * @param reason      The reason for the failure.
     */
    public LocatorException(String locatorName, String reason) {
        super(String.format("Locator '%s' error: %s", locatorName, reason));
    }
}
