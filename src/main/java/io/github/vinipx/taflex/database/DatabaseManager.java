package io.github.vinipx.taflex.database;

import io.github.vinipx.taflex.core.config.ConfigManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database manager with connection pooling using HikariCP.
 * Provides SQL execution capabilities for test data setup and validation.
 */
public class DatabaseManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    
    private static HikariDataSource dataSource;
    private static DatabaseManager instance;
    
    private DatabaseManager() {
        initializePool();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Initialize connection pool
     */
    private void initializePool() {
        String dbUrl = ConfigManager.getProperty("db.url");
        String username = ConfigManager.getProperty("db.username");
        String password = ConfigManager.getProperty("db.password");
        int poolSize = ConfigManager.getIntProperty("db.pool.size", 10);
        
        if (dbUrl == null || dbUrl.isEmpty()) {
            logger.warn("Database URL not configured. Database features disabled.");
            return;
        }
        
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(poolSize);
            config.setMinimumIdle(2);
            config.setIdleTimeout(300000); // 5 minutes
            config.setConnectionTimeout(20000); // 20 seconds
            config.setLeakDetectionThreshold(60000); // 1 minute
            
            // Pool name for monitoring
            config.setPoolName("TAFLEXPool");
            
            dataSource = new HikariDataSource(config);
            
            logger.info("Database connection pool initialized: {}", dbUrl);
            
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    /**
     * Get connection from pool
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not configured");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Execute SELECT query and return results as list of maps
     * @param sql SQL query
     * @return List of rows, each row is a map of column names to values
     */
    public List<Map<String, Object>> executeQuery(String sql) {
        return executeQuery(sql, new Object[0]);
    }
    
    /**
     * Execute SELECT query with parameters
     * @param sql SQL query with ? placeholders
     * @param params Query parameters
     * @return List of rows
     */
    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            logger.debug("Executing query: {}", sql);
            
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
            
            logger.debug("Query returned {} rows", results.size());
            
        } catch (SQLException e) {
            logger.error("Query execution failed: {}", sql, e);
            throw new RuntimeException("Query execution failed", e);
        }
        
        return results;
    }
    
    /**
     * Execute INSERT, UPDATE, DELETE query
     * @param sql SQL statement
     * @param params Statement parameters
     * @return Number of affected rows
     */
    public int executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            logger.debug("Executing update: {}", sql);
            
            int affectedRows = stmt.executeUpdate();
            logger.debug("Update affected {} rows", affectedRows);
            
            return affectedRows;
            
        } catch (SQLException e) {
            logger.error("Update execution failed: {}", sql, e);
            throw new RuntimeException("Update execution failed", e);
        }
    }
    
    /**
     * Execute query and return single value
     * @param sql SQL query
     * @param params Query parameters
     * @return Single value or null
     */
    public Object executeScalar(String sql, Object... params) {
        List<Map<String, Object>> results = executeQuery(sql, params);
        
        if (results.isEmpty()) {
            return null;
        }
        
        // Return first column of first row
        return results.get(0).values().iterator().next();
    }
    
    /**
     * Execute batch insert/update
     * @param sql SQL statement
     * @param batchParams List of parameter arrays
     * @return Array of update counts
     */
    public int[] executeBatch(String sql, List<Object[]> batchParams) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (Object[] params : batchParams) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                stmt.addBatch();
            }
            
            logger.debug("Executing batch of {} statements", batchParams.size());
            
            int[] results = stmt.executeBatch();
            conn.commit();
            
            logger.debug("Batch execution complete");
            
            return results;
            
        } catch (SQLException e) {
            logger.error("Batch execution failed", e);
            throw new RuntimeException("Batch execution failed", e);
        }
    }
    
    /**
     * Execute within transaction
     * @param callback Transaction callback
     * @param <T> Return type
     * @return Result from callback
     */
    public <T> T executeInTransaction(TransactionCallback<T> callback) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                T result = callback.doInTransaction(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Transaction failed", e);
            }
            
        } catch (SQLException e) {
            logger.error("Transaction execution failed", e);
            throw new RuntimeException("Transaction execution failed", e);
        }
    }
    
    /**
     * Close connection pool
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
    
    /**
     * Check if database is configured and available
     * @return true if database is available
     */
    public boolean isAvailable() {
        if (dataSource == null) {
            return false;
        }
        
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Transaction callback interface
     */
    @FunctionalInterface
    public interface TransactionCallback<T> {
        T doInTransaction(Connection conn) throws SQLException;
    }
}