package com.enterprise.taflex.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG annotation transformer to automatically apply retry analyzer to all tests.
 */
public class AnnotationTransformer implements IAnnotationTransformer {
    
    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                         Constructor testConstructor, Method testMethod) {
        // Apply retry analyzer if not already set
        if (annotation.getRetryAnalyzerClass() == null) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}