package io.github.vinipx.taflex.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

/**
 * TestNG listener for comprehensive test execution logging and reporting.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestListener implements ITestListener, ISuiteListener, IInvokedMethodListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
    private static final String SUITE_SEPARATOR = "==================================================";
    private static final String TEST_SEPARATOR = "--------------------------------------------------";
    
    private long suiteStartTime;
    private long testStartTime;
    
    @Override
    public void onStart(ISuite suite) {
        suiteStartTime = System.currentTimeMillis();
        logger.info(SUITE_SEPARATOR);
        logger.info("Suite Started: {}", suite.getName());
        logger.info(SUITE_SEPARATOR);
    }
    
    @Override
    public void onFinish(ISuite suite) {
        long duration = System.currentTimeMillis() - suiteStartTime;
        logger.info(SUITE_SEPARATOR);
        logger.info("Suite Finished: {}", suite.getName());
        logger.info("Total Duration: {} seconds", duration / 1000.0);
        
        // Log suite results
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        for (String testName : suite.getResults().keySet()) {
            ISuiteResult result = suite.getResults().get(testName);
            passed += result.getTestContext().getPassedTests().size();
            failed += result.getTestContext().getFailedTests().size();
            skipped += result.getTestContext().getSkippedTests().size();
        }
        
        logger.info("Results - Passed: {}, Failed: {}, Skipped: {}", passed, failed, skipped);
        logger.info(SUITE_SEPARATOR);
    }
    
    @Override
    public void onStart(ITestContext context) {
        testStartTime = System.currentTimeMillis();
        logger.info(TEST_SEPARATOR);
        logger.info("Test Started: {}", context.getName());
        logger.info(TEST_SEPARATOR);
    }
    
    @Override
    public void onFinish(ITestContext context) {
        long duration = System.currentTimeMillis() - testStartTime;
        logger.info(TEST_SEPARATOR);
        logger.info("Test Finished: {}", context.getName());
        logger.info("Duration: {} seconds", duration / 1000.0);
        logger.info("Passed: {}, Failed: {}, Skipped: {}",
            context.getPassedTests().size(),
            context.getFailedTests().size(),
            context.getSkippedTests().size());
        logger.info(TEST_SEPARATOR);
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        logger.info("[START] {}.{}()", 
            result.getTestClass().getRealClass().getSimpleName(),
            result.getName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        logger.info("[PASS] {}.{}() - {} ms", 
            result.getTestClass().getRealClass().getSimpleName(),
            result.getName(),
            duration);
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        Throwable throwable = result.getThrowable();
        
        logger.error("[FAIL] {}.{}() - {} ms", 
            result.getTestClass().getRealClass().getSimpleName(),
            result.getName(),
            duration);
        
        if (throwable != null) {
            logger.error("Exception: {}", throwable.getClass().getName());
            logger.error("Message: {}", throwable.getMessage());
            
            // Log stack trace for debugging
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : throwable.getStackTrace()) {
                stackTrace.append("\n  at ").append(element);
                // Limit stack trace length
                if (stackTrace.length() > 2000) {
                    stackTrace.append("\n  ... (truncated)");
                    break;
                }
            }
            logger.debug("Stack trace:{}", stackTrace);
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("[SKIP] {}.{}()", 
            result.getTestClass().getRealClass().getSimpleName(),
            result.getName());
        
        if (result.getThrowable() != null) {
            logger.warn("Skip reason: {}", result.getThrowable().getMessage());
        }
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("[PARTIAL] {}.{}() - Failed but within success percentage", 
            result.getTestClass().getRealClass().getSimpleName(),
            result.getName());
    }
    
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        // Pre-invocation logic if needed
    }
    
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        // Post-invocation logic if needed
    }
}