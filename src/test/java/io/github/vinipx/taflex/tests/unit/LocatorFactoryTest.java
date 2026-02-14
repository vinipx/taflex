package io.github.vinipx.taflex.tests.unit;

import io.github.vinipx.taflex.core.locators.LocatorFactory;
import io.github.vinipx.taflex.core.locators.LocatorStrategy;
import io.github.vinipx.taflex.core.locators.strategies.JsonLocatorStrategy;
import io.github.vinipx.taflex.core.locators.strategies.PropertiesLocatorStrategy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LocatorFactoryTest {

    private static final String PROPERTIES = "properties";

    @BeforeMethod
    public void setUp() {
        LocatorFactory.clearCache();
    }

    @Test
    public void testGetPropertiesStrategy() {
        LocatorStrategy strategy = LocatorFactory.getLocatorStrategy(PROPERTIES);
        Assert.assertTrue(strategy instanceof PropertiesLocatorStrategy);
        Assert.assertEquals(strategy.getSourceType(), PROPERTIES);
    }

    @Test
    public void testGetJsonStrategy() {
        LocatorStrategy strategy = LocatorFactory.getLocatorStrategy("json");
        Assert.assertTrue(strategy instanceof JsonLocatorStrategy);
        Assert.assertEquals(strategy.getSourceType(), "json");
    }

    @Test
    public void testDefaultStrategy() {
        LocatorStrategy strategy = LocatorFactory.getLocatorStrategy("invalid");
        Assert.assertTrue(strategy instanceof PropertiesLocatorStrategy);
    }

    @Test
    public void testStrategyCaching() {
        LocatorStrategy strategy1 = LocatorFactory.getLocatorStrategy(PROPERTIES);
        LocatorStrategy strategy2 = LocatorFactory.getLocatorStrategy(PROPERTIES);
        Assert.assertSame(strategy1, strategy2, "Strategies should be cached and returned as the same instance");
    }
}
