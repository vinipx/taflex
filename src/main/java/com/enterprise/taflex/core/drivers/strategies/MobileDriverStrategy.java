package com.enterprise.taflex.core.drivers.strategies;

import com.enterprise.taflex.core.config.ConfigManager;
import com.enterprise.taflex.core.drivers.AutomationDriver;
import com.enterprise.taflex.core.drivers.elements.Element;
import com.enterprise.taflex.core.drivers.elements.MobileElement;
import com.enterprise.taflex.core.exceptions.DriverException;
import com.enterprise.taflex.core.locators.LocatorFactory;
import com.enterprise.taflex.core.locators.LocatorStrategy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Mobile automation driver implementation using Appium.
 * Supports both Android and iOS platforms.
 */
public class MobileDriverStrategy implements AutomationDriver {
    
    private static final Logger logger = LoggerFactory.getLogger(MobileDriverStrategy.class);
    
    private AppiumDriver driver;
    private LocatorStrategy locatorStrategy;
    private String platform;
    
    @Override
    public void initialize() {
        logger.info("Initializing Mobile driver (Appium)");
        
        try {
            // Get configuration
            platform = ConfigManager.getProperty("mobile.platform", "android").toLowerCase();
            String appiumUrl = ConfigManager.getProperty("mobile.appium.url", "http://localhost:4723");
            String appPath = ConfigManager.getProperty("mobile.app.path");
            String deviceName = ConfigManager.getProperty("mobile.device.name");
            
            // Build capabilities
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("platformName", platform);
            
            if (deviceName != null && !deviceName.isEmpty()) {
                caps.setCapability("deviceName", deviceName);
            }
            
            if (appPath != null && !appPath.isEmpty()) {
                caps.setCapability("app", appPath);
            }
            
            // Platform-specific capabilities
            if (platform.equals("android")) {
                caps.setCapability("automationName", "UiAutomator2");
                caps.setCapability("appPackage", ConfigManager.getProperty("mobile.app.package"));
                caps.setCapability("appActivity", ConfigManager.getProperty("mobile.app.activity"));
            } else if (platform.equals("ios")) {
                caps.setCapability("automationName", "XCUITest");
                caps.setCapability("bundleId", ConfigManager.getProperty("mobile.app.bundleId"));
            }
            
            // Create driver
            URL url = new URL(appiumUrl);
            
            if (platform.equals("android")) {
                driver = new AndroidDriver(url, caps);
            } else if (platform.equals("ios")) {
                driver = new IOSDriver(url, caps);
            } else {
                throw new DriverException("Unsupported platform: " + platform);
            }
            
            // Set implicit wait
            int timeout = ConfigManager.getTimeout();
            driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(timeout));
            
            // Initialize locator strategy
            locatorStrategy = LocatorFactory.getLocatorStrategy();
            
            logger.info("Mobile driver initialized successfully for platform: {}", platform);
            
        } catch (MalformedURLException e) {
            logger.error("Invalid Appium URL", e);
            throw new DriverException("Invalid Appium URL", e);
        } catch (Exception e) {
            logger.error("Failed to initialize Mobile driver", e);
            throw new DriverException("Failed to initialize Mobile driver", e);
        }
    }
    
    @Override
    public void terminate() {
        logger.info("Terminating Mobile driver");
        
        try {
            if (driver != null) {
                driver.quit();
            }
            logger.info("Mobile driver terminated successfully");
        } catch (Exception e) {
            logger.error("Error terminating Mobile driver", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getNativeDriver() {
        return (T) driver;
    }
    
    @Override
    public void navigateTo(String urlKey) {
        // For mobile, this typically opens a URL in the browser or deep links
        String url = locatorStrategy.resolve(urlKey);
        logger.info("Navigating to: {}", url);
        driver.get(url);
    }
    
    @Override
    public Element findElement(String logicalName) {
        String selector = locatorStrategy.resolve(logicalName);
        logger.debug("Finding element: {} -> {}", logicalName, selector);
        
        org.openqa.selenium.WebElement nativeElement;
        
        // Determine selector type
        if (selector.startsWith("//") || selector.startsWith("/")) {
            // XPath
            nativeElement = driver.findElement(org.openqa.selenium.By.xpath(selector));
        } else if (selector.startsWith("#")) {
            // ID
            nativeElement = driver.findElement(org.openqa.selenium.By.id(selector.substring(1)));
        } else if (selector.startsWith(".")) {
            // Class name
            nativeElement = driver.findElement(org.openqa.selenium.By.className(selector.substring(1)));
        } else if (selector.startsWith("@")) {
            // Accessibility ID (mobile-specific)
            nativeElement = driver.findElement(org.openqa.selenium.By.id(selector.substring(1)));
        } else {
            // Default to accessibility ID or text
            nativeElement = driver.findElement(org.openqa.selenium.By.xpath(
                String.format("//*[@text='%s' or @content-desc='%s']", selector, selector)));
        }
        
        return new MobileElement(nativeElement, logicalName, driver);
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
        
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destPath = Paths.get(path);
            Files.createDirectories(destPath.getParent());
            Files.copy(screenshot.toPath(), destPath);
            return path;
        } catch (Exception e) {
            logger.error("Failed to capture screenshot", e);
            return null;
        }
    }
    
    @Override
    public String getExecutionMode() {
        return "mobile";
    }
    
    /**
     * Get the platform name (android or ios)
     * @return Platform name
     */
    public String getPlatform() {
        return platform;
    }
    
    /**
     * Hide keyboard (if shown)
     */
    public void hideKeyboard() {
        if (driver instanceof AndroidDriver) {
            ((AndroidDriver) driver).hideKeyboard();
        }
    }
    
    /**
     * Swipe from one point to another
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param endX Ending X coordinate
     * @param endY Ending Y coordinate
     */
    public void swipe(int startX, int startY, int endX, int endY) {
        // Implementation depends on Appium version
        // For newer versions, use W3C Actions
        logger.info("Swiping from ({},{}) to ({},{})", startX, startY, endX, endY);
        // Add swipe implementation here based on your Appium version
    }
    
    /**
     * Install app (requires AndroidDriver or IOSDriver)
     * @param appPath Path to the app file
     */
    public void installApp(String appPath) {
        if (driver instanceof AndroidDriver) {
            ((AndroidDriver) driver).installApp(appPath);
        }
        // Note: IOSDriver may have different method signature
    }
    
    /**
     * Launch app (Android only in Appium 9.x)
     */
    public void launchApp() {
        if (driver instanceof AndroidDriver) {
            ((AndroidDriver) driver).activateApp(
                ConfigManager.getProperty("mobile.app.package")
            );
        }
    }
    
    /**
     * Close app (Android only in Appium 9.x)
     */
    public void closeApp() {
        if (driver instanceof AndroidDriver) {
            ((AndroidDriver) driver).terminateApp(
                ConfigManager.getProperty("mobile.app.package")
            );
        }
    }
}