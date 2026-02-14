package io.github.vinipx.taflex.core.exceptions;

/**
 * Base exception for driver-related errors
 */
public class DriverException extends RuntimeException {
    
    public DriverException(String message) {
        super(message);
    }
    
    public DriverException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DriverException(Throwable cause) {
        super(cause);
    }
}