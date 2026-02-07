package com.enterprise.taflex.listeners;

import com.enterprise.taflex.core.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry analyzer for handling flaky tests.
 * Retries failed tests based on configuration.
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);
    
    private int retryCount = 0;
    private final int maxRetryCount;
    private final boolean retryEnabled;
    
    public RetryAnalyzer() {
        this.retryEnabled = ConfigManager.getBooleanProperty("retry.enabled", true);
        this.maxRetryCount = ConfigManager.getIntProperty("retry.max.attempts", 2);
    }
    
    @Override
    public boolean retry(ITestResult result) {
        if (!retryEnabled) {
            return false;
        }
        
        if (retryCount < maxRetryCount) {
            retryCount++;
            logger.warn("Retrying test: {}.{}() - Attempt {} of {}",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getName(),
                retryCount,
                maxRetryCount);
            
            // Log the failure reason
            if (result.getThrowable() != null) {
                logger.warn("Previous failure: {}", result.getThrowable().getMessage());
            }
            
            return true;
        }
        
        logger.error("Test failed after {} retry attempts: {}.{}()",
            maxRetryCount,
            result.getTestClass().getRealClass().getSimpleName(),
            result.getName());
        
        return false;
    }
    
    /**
     * Reset retry count (called by test framework)
     */
    public void reset() {
        retryCount = 0;
    }
}