package io.github.vinipx.taflex.core.drivers.strategies;

import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.drivers.AutomationDriver;
import io.github.vinipx.taflex.core.drivers.elements.Element;
import io.github.vinipx.taflex.core.drivers.elements.PlaywrightElement;
import io.github.vinipx.taflex.core.exceptions.DriverException;
import io.github.vinipx.taflex.core.locators.LocatorFactory;
import io.github.vinipx.taflex.core.locators.LocatorStrategy;
import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * Web automation driver implementation using Microsoft Playwright.
 */
public class PlaywrightDriverStrategy implements AutomationDriver {
    
    private static final Logger logger = LoggerFactory.getLogger(PlaywrightDriverStrategy.class);
    
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private LocatorStrategy locatorStrategy;
    
    @Override
    public void initialize() {
        logger.info("Initializing Playwright driver");
        
        try {
            // Initialize Playwright
            playwright = Playwright.create();
            
            // Get browser type from config
            String browserType = ConfigManager.getProperty("web.browser", "chromium").toLowerCase();
            boolean headless = ConfigManager.isHeadless();
            
            logger.info("Launching {} browser (headless: {})", browserType, headless);
            
            // Launch browser
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless);
            
            switch (browserType) {
                case "chromium":
                    browser = playwright.chromium().launch(launchOptions);
                    break;
                case "firefox":
                    browser = playwright.firefox().launch(launchOptions);
                    break;
                case "webkit":
                    browser = playwright.webkit().launch(launchOptions);
                    break;
                default:
                    throw new DriverException("Unsupported browser type: " + browserType);
            }
            
            // Create browser context with viewport
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080);
            
            context = browser.newContext(contextOptions);
            
            // Enable tracing if needed
            if (ConfigManager.getBooleanProperty("web.tracing.enabled", false)) {
                context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
            }
            
            // Create page
            page = context.newPage();
            
            // Set default timeout
            int timeout = ConfigManager.getTimeout();
            page.setDefaultTimeout(timeout * 1000);
            
            // Initialize locator strategy
            locatorStrategy = LocatorFactory.getLocatorStrategy();
            
            logger.info("Playwright driver initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize Playwright driver", e);
            throw new DriverException("Failed to initialize Playwright driver", e);
        }
    }
    
    @Override
    public void terminate() {
        logger.info("Terminating Playwright driver");
        
        try {
            if (page != null) {
                page.close();
            }
            if (context != null) {
                context.close();
            }
            if (browser != null) {
                browser.close();
            }
            if (playwright != null) {
                playwright.close();
            }
            logger.info("Playwright driver terminated successfully");
        } catch (Exception e) {
            logger.error("Error terminating Playwright driver", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getNativeDriver() {
        return (T) page;
    }
    
    @Override
    public void navigateTo(String urlKey) {
        String url = locatorStrategy.resolve(urlKey);
        logger.info("Navigating to: {}", url);
        page.navigate(url);
    }
    
    @Override
    public Element findElement(String logicalName) {
        String selector = locatorStrategy.resolve(logicalName);
        logger.debug("Finding element: {} -> {}", logicalName, selector);
        return new PlaywrightElement(page.locator(selector), logicalName);
    }
    
    @Override
    public void click(String logicalName) {
        findElement(logicalName).click();
    }
    
    @Override
    public void type(String logicalName, String text) {
        findElement(logicalName).type(text);
    }
    
    @Override
    public String getText(String logicalName) {
        return findElement(logicalName).getText();
    }
    
    @Override
    public boolean isVisible(String logicalName) {
        return findElement(logicalName).isVisible();
    }
    
    @Override
    public void waitForVisible(String logicalName, int timeoutSeconds) {
        findElement(logicalName).waitForVisible(timeoutSeconds);
    }
    
    @Override
    public String captureScreenshot(String fileName) {
        String path = "screenshots/" + fileName + "_" + System.currentTimeMillis() + ".png";
        logger.info("Capturing screenshot: {}", path);
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(Paths.get(path))
            .setFullPage(true));
        return path;
    }
    
    @Override
    public String getExecutionMode() {
        return "web";
    }
    
    /**
     * Get the Playwright Page object for advanced operations
     * @return Page instance
     */
    public Page getPage() {
        return page;
    }
    
    /**
     * Get the BrowserContext for advanced operations
     * @return BrowserContext instance
     */
    public BrowserContext getContext() {
        return context;
    }
}