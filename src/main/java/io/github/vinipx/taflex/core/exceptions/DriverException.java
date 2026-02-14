package io.github.vinipx.taflex.core.exceptions;

/**
 * Base runtime exception for all automation driver-related errors.
 *
 * <p>This exception is thrown when driver initialization fails, a native tool
 * returns an error, or an unsupported execution mode is requested.
 */
public class DriverException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a DriverException with a detailed message.
     *
     * @param message The error message.
     */
    public DriverException(String message) {
        super(message);
    }
    
    /**
     * Constructs a DriverException with a message and an underlying cause.
     *
     * @param message The error message.
     * @param cause   The root cause exception.
     */
    public DriverException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a DriverException with only a root cause.
     *
     * @param cause The root cause exception.
     */
    public DriverException(Throwable cause) {
        super(cause);
    }
}
