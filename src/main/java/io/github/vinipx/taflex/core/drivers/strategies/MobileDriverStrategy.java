package io.github.vinipx.taflex.core.drivers.strategies;

import io.github.vinipx.taflex.core.config.ConfigManager;
import io.github.vinipx.taflex.core.drivers.AutomationDriver;
import io.github.vinipx.taflex.core.drivers.elements.Element;
import io.github.vinipx.taflex.core.drivers.elements.MobileElement;
import io.github.vinipx.taflex.core.exceptions.DriverException;
import io.github.vinipx.taflex.core.locators.LocatorFactory;
import io.github.vinipx.taflex.core.locators.LocatorStrategy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Mobile automation driver implementation using Appium.
 *
 * <p>Provides support for both Android and iOS platforms. Includes features for
 * automatic platform detection via local tools (adb, idevice_id) and automated
 * Appium server lifecycle management.
 */
public class MobileDriverStrategy implements AutomationDriver {
    
    private static final Logger logger = LoggerFactory.getLogger(MobileDriverStrategy.class);
    
    private AppiumDriver driver;
    private LocatorStrategy locatorStrategy;
    private MobilePlatform platform;
    private Process appiumProcess;
    private boolean appiumStartedByFramework;

    /**
     * Supported mobile platforms.
     */
    private enum MobilePlatform {
        ANDROID("Android"),
        IOS("iOS");

        private final String capabilityName;

        MobilePlatform(String capabilityName) {
            this.capabilityName = capabilityName;
        }

        public String getCapabilityName() {
            return capabilityName;
        }

        /**
         * Resolves platform from string value.
         *
         * @param value "android" or "ios".
         * @return The corresponding MobilePlatform.
         */
        public static MobilePlatform from(String value) {
            if (value == null) {
                return ANDROID;
            }
            String normalized = value.trim().toLowerCase(Locale.ROOT);
            if ("android".equals(normalized)) {
                return ANDROID;
            }
            if ("ios".equals(normalized)) {
                return IOS;
            }
            throw new DriverException("Unsupported platform: " + value);
        }
    }
    
    @Override
    public void initialize() {
        logger.info("Initializing Mobile driver (Appium)");
        
        try {
            platform = resolvePlatform();
            String appiumUrl = ConfigManager.getProperty("mobile.appium.url", "http://localhost:4723");
            String appPath = ConfigManager.getProperty("mobile.app.path");
            String deviceName = ConfigManager.getProperty("mobile.device.name");

            ensureAppiumServer(appiumUrl);
            
            DesiredCapabilities caps = buildCapabilities(platform, deviceName, appPath);
            URL url = new URL(appiumUrl);
            driver = createDriver(url, platform, caps);
            
            int timeout = ConfigManager.getTimeout();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
            
            locatorStrategy = LocatorFactory.getLocatorStrategy();
            
            logger.info("Mobile driver initialized successfully for platform: {}", platform.name().toLowerCase(Locale.ROOT));
            
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
            stopAppiumIfStarted();
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
        String url = locatorStrategy.resolve(urlKey);
        logger.info("Navigating to: {}", url);
        driver.get(url);
    }
    
    @Override
    public Element findElement(String logicalName) {
        String selector = locatorStrategy.resolve(logicalName);
        logger.debug("Finding element: {} -> {}", logicalName, selector);
        
        org.openqa.selenium.WebElement nativeElement;
        
        if (selector.startsWith("//") || selector.startsWith("/")) {
            nativeElement = driver.findElement(org.openqa.selenium.By.xpath(selector));
        } else if (selector.startsWith("#")) {
            nativeElement = driver.findElement(org.openqa.selenium.By.id(selector.substring(1)));
        } else if (selector.startsWith(".")) {
            nativeElement = driver.findElement(org.openqa.selenium.By.className(selector.substring(1)));
        } else if (selector.startsWith("@")) {
            nativeElement = driver.findElement(org.openqa.selenium.By.id(selector.substring(1)));
        } else {
            nativeElement = driver.findElement(org.openqa.selenium.By.xpath(
                String.format("//*[@text='%s' or @content-desc='%s']", selector, selector)));
        }
        
        return new MobileElement(nativeElement, logicalName);
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
            File screenshot = driver.getScreenshotAs(OutputType.FILE);
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
     * Gets the platform name (android or ios).
     *
     * @return Platform name string.
     */
    public String getPlatform() {
        return platform == null ? null : platform.name().toLowerCase(Locale.ROOT);
    }
    
    /**
     * Attempts to hide the soft keyboard.
     */
    public void hideKeyboard() {
        if (driver instanceof AndroidDriver) {
            ((AndroidDriver) driver).hideKeyboard();
        }
    }
    
    /**
     * Performs a swipe gesture between two points.
     *
     * @param startX Starting X.
     * @param startY Starting Y.
     * @param endX   Ending X.
     * @param endY   Ending Y.
     */
    public void swipe(int startX, int startY, int endX, int endY) {
        logger.info("Swiping from ({},{}) to ({},{})", startX, startY, endX, endY);
        // Implementation would typically use W3C Actions
    }
    
    /**
     * Installs an application file on the device.
     *
     * @param appPath Path to .apk or .app/.ipa.
     */
    public void installApp(String appPath) {
        if (driver instanceof AndroidDriver) {
            ((AndroidDriver) driver).installApp(appPath);
        }
    }
    
    /**
     * Launches or activates the application under test.
     */
    public void launchApp() {
        if (driver instanceof AndroidDriver) {
            ((AndroidDriver) driver).activateApp(
                ConfigManager.getProperty("mobile.app.package")
            );
        }
    }
    
    /**
     * Terminates the application under test.
     */
    public void closeApp() {
        if (driver instanceof AndroidDriver) {
            ((AndroidDriver) driver).terminateApp(
                ConfigManager.getProperty("mobile.app.package")
            );
        }
    }

    /**
     * Builds DesiredCapabilities based on platform and configuration.
     */
    private DesiredCapabilities buildCapabilities(MobilePlatform platform, String deviceName, String appPath) {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", platform.getCapabilityName());

        if (deviceName != null && !deviceName.isEmpty()) {
            caps.setCapability("deviceName", deviceName);
        }

        if (appPath != null && !appPath.isEmpty()) {
            caps.setCapability("app", appPath);
        }

        if (platform == MobilePlatform.ANDROID) {
            return buildAndroidCapabilities(caps);
        }
        return buildIosCapabilities(caps);
    }

    private DesiredCapabilities buildAndroidCapabilities(DesiredCapabilities caps) {
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("appPackage", ConfigManager.requireProperty("mobile.app.package"));
        caps.setCapability("appActivity", ConfigManager.requireProperty("mobile.app.activity"));
        return caps;
    }

    private DesiredCapabilities buildIosCapabilities(DesiredCapabilities caps) {
        caps.setCapability("automationName", "XCUITest");
        caps.setCapability("bundleId", ConfigManager.requireProperty("mobile.app.bundleId"));
        return caps;
    }

    private AppiumDriver createDriver(URL url, MobilePlatform platform, DesiredCapabilities caps) {
        if (platform == MobilePlatform.ANDROID) {
            return new AndroidDriver(url, caps);
        }
        if (platform == MobilePlatform.IOS) {
            return new IOSDriver(url, caps);
        }
        throw new DriverException("Unsupported platform: " + platform);
    }

    /**
     * Decides which platform to use based on config or auto-detection.
     */
    private MobilePlatform resolvePlatform() {
        boolean autoDetect = ConfigManager.getBooleanProperty("mobile.platform.auto", true);
        String configuredPlatform = ConfigManager.getProperty("mobile.platform", "android");

        if (!autoDetect) {
            logger.info("Mobile platform auto-detection disabled; using {}", configuredPlatform);
            return MobilePlatform.from(configuredPlatform);
        }

        MobilePlatform detected = detectPlatformFromLocalTools();
        if (detected != null) {
            logger.info("Detected mobile platform via local tools: {}", detected.name().toLowerCase(Locale.ROOT));
            return detected;
        }

        logger.info("No mobile platform detected; using {}", configuredPlatform);
        return MobilePlatform.from(configuredPlatform);
    }

    private MobilePlatform detectPlatformFromLocalTools() {
        boolean androidConnected = hasAndroidDevice();
        boolean iosConnected = hasIosDevice();

        if (androidConnected && iosConnected) {
            logger.warn("Both Android and iOS devices detected; falling back to configured platform");
            return null;
        }
        if (androidConnected) {
            return MobilePlatform.ANDROID;
        }
        if (iosConnected) {
            return MobilePlatform.IOS;
        }
        return null;
    }

    private boolean hasAndroidDevice() {
        String output = runCommand("adb", "devices");
        if (output.isEmpty()) {
            return false;
        }
        String[] lines = output.split("\\R");
        for (String line : lines) {
            if (line.startsWith("List of devices")) {
                continue;
            }
            if (line.contains("\tdevice")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasIosDevice() {
        String output = runCommand("idevice_id", "-l");
        if (output.isEmpty()) {
            return false;
        }
        String[] lines = output.split("\\R");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private String runCommand(String... command) {
        Process process = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            process = builder.start();

            boolean finished = process.waitFor(3, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return "";
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                return output.toString();
            }
        } catch (IOException e) {
            logger.debug("Command not available: {}", String.join(" ", command), e);
            return "";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.debug("Command interrupted: {}", String.join(" ", command), e);
            return "";
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    /**
     * Validates that the Appium server is responsive, or starts it if auto-start is enabled.
     */
    private void ensureAppiumServer(String appiumUrl) {
        if (isAppiumRunning(appiumUrl)) {
            logger.info("Appium server is running at {}", appiumUrl);
            return;
        }

        boolean autoStart = ConfigManager.getBooleanProperty("mobile.appium.auto.start", false);
        if (!autoStart) {
            throw new DriverException("Appium server is not running. Start it manually or enable mobile.appium.auto.start.");
        }

        startAppium(appiumUrl);

        int timeoutSeconds = ConfigManager.getIntProperty("mobile.appium.start.timeout.seconds", 30);
        Instant deadline = Instant.now().plus(Duration.ofSeconds(timeoutSeconds));
        while (Instant.now().isBefore(deadline)) {
            if (isAppiumRunning(appiumUrl)) {
                logger.info("Appium server started successfully");
                return;
            }
            if (appiumProcess != null && !appiumProcess.isAlive()) {
                throw new DriverException("Appium server process exited before becoming ready.");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DriverException("Interrupted while waiting for Appium server to start.", e);
            }
        }

        throw new DriverException("Timed out waiting for Appium server to start.");
    }

    private boolean isAppiumRunning(String appiumUrl) {
        String statusPath = ConfigManager.getProperty("mobile.appium.status.path", "/status");
        String statusUrl = buildStatusUrl(appiumUrl, statusPath);
        HttpURLConnection connection = null;
        try {
            URL url = new URL(statusUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            int responseCode = connection.getResponseCode();
            return responseCode >= 200 && responseCode < 300;
        } catch (IOException e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String buildStatusUrl(String appiumUrl, String statusPath) {
        String base = appiumUrl;
        String path = statusPath == null ? "/status" : statusPath.trim();
        if (path.isEmpty()) {
            path = "/status";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (base.endsWith("/") && path.startsWith("/")) {
            return base.substring(0, base.length() - 1) + path;
        }
        if (!base.endsWith("/") && !path.startsWith("/")) {
            return base + "/" + path;
        }
        return base + path;
    }

    /**
     * Starts the Appium server process using configured command and arguments.
     */
    private void startAppium(String appiumUrl) {
        String command = ConfigManager.getProperty("mobile.appium.start.command", "appium");
        String args = ConfigManager.getProperty("mobile.appium.start.args", "");

        List<String> commandParts = new ArrayList<>();
        commandParts.add(command);
        if (args != null && !args.trim().isEmpty()) {
            String[] splitArgs = args.trim().split("\\s+");
            for (String arg : splitArgs) {
                if (!arg.isEmpty()) {
                    commandParts.add(arg);
                }
            }
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(commandParts);
            builder.redirectErrorStream(true);
            appiumProcess = builder.start();
            appiumStartedByFramework = true;
            logger.info("Starting Appium server with command: {}", String.join(" ", commandParts));
            logger.info("Waiting for Appium server at {}", appiumUrl);
        } catch (IOException e) {
            throw new DriverException("Failed to start Appium server. Ensure Appium is installed and the command is valid.", e);
        }
    }

    private void stopAppiumIfStarted() {
        if (appiumStartedByFramework && appiumProcess != null && appiumProcess.isAlive()) {
            appiumProcess.destroy();
            try {
                if (!appiumProcess.waitFor(5, TimeUnit.SECONDS)) {
                    appiumProcess.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                appiumProcess.destroyForcibly();
            }
        }
    }
}
