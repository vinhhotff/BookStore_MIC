package com.example.bookstore.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods (typically service layer operations)
 * that should be retried upon encountering transient optimistic locking / concurrency conflicts.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryOnConflict {
    /**
     * Maximum number of execution attempts.
     */
    int maxRetries() default 3;

    /**
     * Delay in milliseconds between retry attempts.
     */
    long delayMs() default 100;
}
