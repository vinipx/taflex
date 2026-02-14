package io.github.vinipx.taflex.core.data;

import java.util.List;
import java.util.Map;

/**
 * Interface defining the contract for test data providers.
 *
 * <p>Implementations of this interface allow tests to load externalized data from
 * various sources such as CSV files, JSON, Excel, or Databases. This promotes
 * data-driven testing and decouples test logic from specific values.
 */
public interface TestDataProvider {
    
    /**
     * Loads a single data set (record) identified by its ID.
     *
     * @param dataSetId The unique identifier for the data set (e.g., "user.admin").
     * @return A Map containing field names as keys and their corresponding values.
     */
    Map<String, String> load(String dataSetId);
    
    /**
     * Loads a table of multiple records identified by a table ID.
     *
     * @param tableId The identifier for the data table (e.g., "product_list").
     * @return A List of Maps, where each Map represents a row in the table.
     */
    List<Map<String, String>> loadTable(String tableId);
    
     /**
     * Retrieves a specific value from a identified data set.
     *
     * @param dataSetId The identifier for the data set.
     * @param fieldName The specific field/column name to retrieve.
     * @return The field value as a String.
     */
    String getValue(String dataSetId, String fieldName);
    
    /**
     * Checks if a specific data set ID exists in the source.
     *
     * @param dataSetId The identifier to check.
     * @return true if the data set exists, false otherwise.
     */
    boolean hasDataSet(String dataSetId);
    
    /**
     * Returns a list of all data set IDs available in the provider.
     *
     * @return A List of strings representing all valid dataSetId values.
     */
    List<String> getAvailableDataSets();
}
