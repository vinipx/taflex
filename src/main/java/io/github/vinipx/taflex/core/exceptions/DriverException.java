package io.github.vinipx.taflex.core.exceptions;

/**
 * Base exception for driver-related errors
 */
public class DriverException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

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