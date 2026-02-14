package io.github.vinipx.taflex.core.locators;

/**
 * Strategy interface for externalizing and resolving UI and API locators.
 *
 * <p>Implementations of this interface allow the framework to load selectors,
 * paths, and endpoints from external files (Properties, JSON) or databases,
 * ensuring test code remains decoupled from the specific implementation of the SUT.
 */
public interface LocatorStrategy {
    
    /**
     * Resolves a logical locator name to an actual platform selector or path.
     *
     * @param logicalName The name defined in the test (e.g., "login.username.field").
     * @return The resolved value (e.g., "#username" or "//input[@id='username']").
     * @throws io.github.vinipx.taflex.core.exceptions.LocatorException If not found.
     */
    String resolve(String logicalName);
    
    /**
     * Explicitly loads locators from a specific source path.
     *
     * @param sourcePath The path to the locator source (e.g., file path).
     */
    void load(String sourcePath);
    
    /**
     * Checks if a specific logical name is present in the current locator cache.
     *
     * @param logicalName The name to verify.
     * @return true if the locator exists, false otherwise.
     */
    boolean hasLocator(String logicalName);
    
    /**
     * Reloads all locators from their respective sources.
     * Useful for dynamic locator updates during a test run.
     */
    void reload();
    
    /**
     * Returns the type identifier of this strategy.
     *
     * @return A string identifier (e.g., "properties", "json").
     */
    String getSourceType();
}
