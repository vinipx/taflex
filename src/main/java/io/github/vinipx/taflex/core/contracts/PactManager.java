package io.github.vinipx.taflex.core.contracts;

import io.github.vinipx.taflex.core.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages Pact contract testing lifecycle.
 * Ported and adapted from taflex-js.
 */
public class PactManager {
    private static final Logger logger = LoggerFactory.getLogger(PactManager.class);
    private final boolean enabled;
    @SuppressWarnings("PMD.UnusedPrivateField")
    private String consumer;
    @SuppressWarnings("PMD.UnusedPrivateField")
    private String provider;

    public PactManager() {
        this.enabled = ConfigManager.getBooleanProperty("pact.enabled", false);
    }

    public void setup(String consumer, String provider) {
        if (!enabled) return;
        this.consumer = consumer;
        this.provider = provider;
        logger.info("Pact enabled for Consumer: {} and Provider: {}", consumer, provider);
    }

    public boolean isEnabled() {
        return enabled;
    }
}
