package io.github.vinipx.taflex.core.data;

import java.util.List;
import java.util.Map;

/**
 * Strategy interface for test data providers.
 * Supports multiple data sources: CSV, Properties, JSON, Database, etc.
 */
public interface TestDataProvider {
    
    /**
     * Load a single data record by ID
     * @param dataSetId Identifier for the data set (e.g., "user.admin")
     * @return Map of field names to values
     */
    Map<String, String> load(String dataSetId);
    
    /**
     * Load multiple records as a table
     * @param tableId Identifier for the data table
     * @return List of records, each as a field map
     */
    List<Map<String, String>> loadTable(String tableId);
    
     /**
     * Get a specific value from a data set
     * @param dataSetId Identifier for the data set
     * @param fieldName Name of the field
     * @return Field value
     */
    String getValue(String dataSetId, String fieldName);
    
    /**
     * Check if a data set exists
     * @param dataSetId Identifier for the data set
     * @return true if exists
     */
    boolean hasDataSet(String dataSetId);
    
    /**
     * Get all available data set IDs
     * @return List of data set identifiers
     */
    List<String> getAvailableDataSets();
}