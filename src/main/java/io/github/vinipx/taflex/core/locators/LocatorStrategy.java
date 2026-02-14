package io.github.vinipx.taflex.core.locators;

/**
 * Strategy interface for resolving locators from external sources.
 * Implementations can load from properties files, databases, or other sources.
 */
public interface LocatorStrategy {
    
    /**
     * Resolve a logical locator name to an actual selector/path
     * @param logicalName The logical name (e.g., "login.username.field")
     * @return The resolved locator (e.g., "#username" or "//input[@id='username']")
     */
    String resolve(String logicalName);
    
    /**
     * Load locators from a source
     * @param sourcePath Path to the locator source (e.g., file path, database connection)
     */
    void load(String sourcePath);
    
    /**
     * Check if a locator exists
     * @param logicalName The logical name to check
     * @return true if the locator exists
     */
    boolean hasLocator(String logicalName);
    
    /**
     * Reload locators (useful for dynamic locator updates)
     */
    void reload();
    
    /**
     * Get the source type (e.g., "properties", "database", "json")
     * @return Source type identifier
     */
    String getSourceType();
}