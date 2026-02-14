package io.github.vinipx.taflex.core.contracts;

import io.github.vinipx.taflex.core.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the Pact contract testing lifecycle within the framework.
 *
 * <p>This manager provides a centralized way to enable, configure, and coordinate
 * consumer-driven contract tests. It is designed to be strategy-agnostic,
 * allowing tests to verify interactions against various API providers.
 */
public class PactManager {
    private static final Logger logger = LoggerFactory.getLogger(PactManager.class);
    private final boolean enabled;
    
    @SuppressWarnings("PMD.UnusedPrivateField")
    private String consumer;
    
    @SuppressWarnings("PMD.UnusedPrivateField")
    private String provider;

    /**
     * Initializes the PactManager by checking the {@code pact.enabled} property.
     */
    public PactManager() {
        this.enabled = ConfigManager.getBooleanProperty("pact.enabled", false);
    }

    /**
     * Configures the consumer and provider for the current contract test session.
     *
     * @param consumer The name of the service consuming the API.
     * @param provider The name of the service providing the API.
     */
    public void setup(String consumer, String provider) {
        if (!enabled) {
            return;
        }
        this.consumer = consumer;
        this.provider = provider;
        logger.info("Pact enabled for Consumer: {} and Provider: {}", consumer, provider);
    }

    /**
     * Checks if Pact contract testing is globally enabled in the configuration.
     *
     * @return true if enabled, false otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }
}
