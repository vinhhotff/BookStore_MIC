package com.example.bookstore.config;

import com.example.bookstore.common.RetryOnConflict;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

/**
 * Aspect to handle retries for methods annotated with {@link RetryOnConflict}
 * when experiencing optimistic locking/concurrency conflicts.
 * <p>
 * This aspect is ordered to execute outside the transactional proxy (using a high priority Order)
 * so that each retry attempt starts in a new, independent transaction.
 */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
@Slf4j
public class RetryAspect {

    @Around("@annotation(retryOnConflict)")
    public Object retryOnConflict(ProceedingJoinPoint joinPoint, RetryOnConflict retryOnConflict) throws Throwable {
        int maxRetries = retryOnConflict.maxRetries();
        long delayMs = retryOnConflict.delayMs();
        Throwable lastException = null;

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.debug("Executing {}.{} - Attempt {}/{}", className, methodName, attempt, maxRetries);
                return joinPoint.proceed();
            } catch (ConcurrencyFailureException ex) {
                lastException = ex;
                log.warn("Concurrency conflict detected in {}.{} (Attempt {}/{}): {}", 
                        className, methodName, attempt, maxRetries, ex.getMessage());

                if (attempt < maxRetries) {
                    try {
                        log.info("Retrying method {}.{} in {}ms...", className, methodName, delayMs);
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw ie;
                    }
                }
            }
        }

        log.error("Failed to execute {}.{} after {} attempts due to persistent concurrency conflicts.", 
                className, methodName, maxRetries);
        throw lastException;
    }
}
