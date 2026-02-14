package io.github.vinipx.taflex.core.contracts;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.model.MockProviderConfig;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.consumer.ConsumerPactBuilder;
import io.github.vinipx.taflex.core.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages Pact contract testing lifecycle.
 * Ported and adapted from taflex-js.
 */
public class PactManager {
    private static final Logger logger = LoggerFactory.getLogger(PactManager.class);
    private final boolean enabled;
    private String consumer;
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
