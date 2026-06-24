package com.example.bookstore.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Service *)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        
        Object[] args = joinPoint.getArgs();
        log.info("ENTRY: {}.{}() with arguments: {}", className, methodName, Arrays.toString(args));
        
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            log.info("EXIT: {}.{}() executed in {} ms with result: {}", className, methodName, executionTime, result);
            return result;
        } catch (Throwable e) {
            long executionTime = System.currentTimeMillis() - start;
            log.error("ERROR: {}.{}() failed in {} ms with exception: {}", className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }
}
