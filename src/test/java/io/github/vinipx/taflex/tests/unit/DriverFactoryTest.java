package io.github.vinipx.taflex.tests.unit;

import io.github.vinipx.taflex.core.drivers.AutomationDriver;
import io.github.vinipx.taflex.core.drivers.DriverFactory;
import io.github.vinipx.taflex.core.drivers.strategies.ApiDriverStrategy;
import io.github.vinipx.taflex.core.drivers.strategies.MobileDriverStrategy;
import io.github.vinipx.taflex.core.drivers.strategies.PlaywrightDriverStrategy;
import io.github.vinipx.taflex.core.exceptions.DriverException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DriverFactoryTest {

    private static final String WEB = "web";
    private static final String API = "api";

    @BeforeMethod
    public void setUp() {
        DriverFactory.clearCache();
    }

    @Test
    public void testGetWebDriver() {
        AutomationDriver driver = DriverFactory.getDriver(WEB);
        Assert.assertTrue(driver instanceof PlaywrightDriverStrategy);
        Assert.assertEquals(driver.getExecutionMode(), WEB);
    }

    @Test
    public void testGetApiDriver() {
        AutomationDriver driver = DriverFactory.getDriver(API);
        Assert.assertTrue(driver instanceof ApiDriverStrategy);
        Assert.assertEquals(driver.getExecutionMode(), API);
    }

    @Test
    public void testGetMobileDriver() {
        AutomationDriver driver = DriverFactory.getDriver("mobile");
        Assert.assertTrue(driver instanceof MobileDriverStrategy);
        Assert.assertEquals(driver.getExecutionMode(), "mobile");
    }

    @Test(expectedExceptions = DriverException.class)
    public void testInvalidDriverMode() {
        DriverFactory.getDriver("invalid");
    }

    @Test
    public void testDriverCaching() {
        AutomationDriver driver1 = DriverFactory.getDriver(WEB);
        AutomationDriver driver2 = DriverFactory.getDriver(WEB);
        Assert.assertSame(driver1, driver2, "Drivers should be cached");
    }

    @Test
    public void testGetDriverWithoutCache() {
        AutomationDriver driver1 = DriverFactory.getDriverWithoutCache(API);
        AutomationDriver driver2 = DriverFactory.getDriverWithoutCache(API);
        Assert.assertNotSame(driver1, driver2, "getDriverWithoutCache should return new instances");
    }
}
