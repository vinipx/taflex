package io.github.vinipx.taflex.tests.unit;

import io.github.vinipx.taflex.core.config.ConfigManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigManagerTest {

    @Test
    public void testDefaultConfig() {
        String mode = ConfigManager.getExecutionMode();
        Assert.assertTrue(mode.matches("web|api|mobile"), "Default mode should be valid");
    }

    @Test
    public void testRequirePropertyFailure() {
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            ConfigManager.requireProperty("non.existent.property.12345");
        });
    }
}
