package io.github.vinipx.taflex.base;

import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.drivers.AutomationDriver;
import io.github.vinipx.taflex.core.drivers.DriverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * Base test class for all test automation.
 * Provides common setup, teardown, and utility methods.
 */
public abstract class BaseTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    
    protected AutomationDriver driver;
    protected String executionMode;
    
    /**
     * Global setup - runs once before all tests in the class
     */
    @BeforeSuite
    public void beforeSuite() {
        logger.info("========================================");
        logger.info("Starting Test Suite");
        logger.info("========================================");
        
        // Log configuration
        executionMode = ConfigManager.getExecutionMode();
        logger.info("Execution Mode: {}", executionMode);
        logger.info("Environment: {}", ConfigManager.getProperty("environment", "not set"));
    }
    
    /**
     * Setup before each test method
     */
    @BeforeMethod
    public void setUp() {
        logger.info("Setting up test...");
        
        // Initialize driver based on execution mode
        driver = DriverFactory.getDriver();
        driver.initialize();
        
        logger.info("Test setup complete");
    }
    
    /**
     * Cleanup after each test method
     */
    @AfterMethod
    public void tearDown(ITestResult result) {
        logger.info("Tearing down test...");
        
        try {
            // Capture screenshot on failure
            if (!result.isSuccess() && ConfigManager.getBooleanProperty("screenshot.on.failure", true)) {
                String testName = result.getName();
                String screenshotPath = driver.captureScreenshot(testName);
                logger.info("Screenshot captured: {}", screenshotPath);
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot", e);
        } finally {
            // Always terminate driver
            if (driver != null) {
                driver.terminate();
            }
        }
        
        // Log test result
        if (result.isSuccess()) {
            logger.info("✓ TEST PASSED: {}", result.getName());
        } else {
            logger.error("✗ TEST FAILED: {}", result.getName());
            if (result.getThrowable() != null) {
                logger.error("Failure reason: {}", result.getThrowable().getMessage());
            }
        }
        
        logger.info("Test teardown complete");
    }
    
    /**
     * Global cleanup - runs once after all tests in the class
     */
    @AfterSuite
    public void afterSuite() {
        logger.info("========================================");
        logger.info("Test Suite Complete");
        logger.info("========================================");
        
        // Clean up driver cache
        DriverFactory.clearCache();
    }
    
    /**
     * Pause execution for specified milliseconds
     * @param milliseconds Time to sleep
     */
    protected void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted", e);
        }
    }
    
    /**
     * Get test execution mode
     * @return Execution mode string
     */
    protected String getExecutionMode() {
        return executionMode;
    }
}