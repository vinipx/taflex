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
 * Singleton database manager providing connection pooling and simplified SQL execution.
 *
 * <p>Uses HikariCP for high-performance connection pooling. Designed to assist in
 * test data preparation, teardown, and database-state assertions during test execution.
 */
public final class DatabaseManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    
    private static HikariDataSource dataSource;
    private static DatabaseManager instance;
    
    /**
     * Private constructor triggers the connection pool initialization.
     */
    private DatabaseManager() {
        initializePool();
    }
    
    /**
     * Returns the singleton instance of the DatabaseManager.
     *
     * @return The DatabaseManager instance.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Configures and starts the HikariCP connection pool using settings from properties.
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
            config.setIdleTimeout(300_000); // 5 minutes
            config.setConnectionTimeout(20_000); // 20 seconds
            config.setLeakDetectionThreshold(60_000); // 1 minute
            config.setPoolName("TAFLEXPool");
            
            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized: {}", dbUrl);
            
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    /**
     * Obtains a raw {@link Connection} from the pool.
     *
     * @return A database connection.
     * @throws SQLException If the database is not configured or connection fails.
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not configured");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Executes a SELECT query without parameters.
     *
     * @param sql The SQL query string.
     * @return A list of rows, where each row is a Map of column names to values.
     */
    public List<Map<String, Object>> executeQuery(String sql) {
        return executeQuery(sql, new Object[0]);
    }
    
    /**
     * Executes a parameterized SELECT query.
     *
     * @param sql    The SQL query with '?' placeholders.
     * @param params Values to bind to the placeholders.
     * @return A list of result rows.
     */
    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
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
     * Executes an INSERT, UPDATE, or DELETE statement.
     *
     * @param sql    The SQL statement.
     * @param params Values to bind to the statement.
     * @return The number of rows affected.
     */
    public int executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
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
     * Executes a query expected to return a single value (e.g., COUNT).
     *
     * @param sql    The SQL query.
     * @param params Query parameters.
     * @return The first column value of the first row, or null if no results.
     */
    public Object executeScalar(String sql, Object... params) {
        List<Map<String, Object>> results = executeQuery(sql, params);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0).values().iterator().next();
    }
    
    /**
     * Executes multiple statements in a batch for high performance.
     *
     * @param sql         The template SQL statement.
     * @param batchParams A list of parameter arrays, one for each execution.
     * @return An array of update counts.
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
     * Executes a series of operations within a single database transaction.
     *
     * @param callback The logic to execute inside the transaction.
     * @param <T>      The return type.
     * @return The result of the callback.
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
     * Closes the connection pool and releases all resources.
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
    
    /**
     * Validates if the database connection is correctly configured and alive.
     *
     * @return true if database is accessible, false otherwise.
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
     * Functional interface for implementing transactional logic.
     *
     * @param <T> The result type.
     */
    @FunctionalInterface
    public interface TransactionCallback<T> {
        /**
         * Logic to be executed within a transaction context.
         *
         * @param conn The active database connection.
         * @return The operation result.
         * @throws SQLException On database error.
         */
        T doInTransaction(Connection conn) throws SQLException;
    }
}
