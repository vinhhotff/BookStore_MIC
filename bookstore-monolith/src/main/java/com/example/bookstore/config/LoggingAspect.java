package com.example.bookstore.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * @Around sẽ "bao bọc" xung quanh các hàm mà chúng ta chỉ định.
     * Ở đây, chúng ta đang chỉ định: Tất cả các hàm bên trong package "controller".
     */
    @Around("execution(* com.example.bookstore..*Controller.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            // CHO PHÉP HÀM CHẠY (Thực thi nghiệp vụ)
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - start;

            // CHỈ BÁO ĐỘNG NẾU CHẠY QUÁ CHẬM (> 500 mili-giây)
            if (executionTime > 500) {
                log.warn("🐢 BÁO ĐỘNG API CHẬM: {} mất {} ms để thực thi!", 
                         joinPoint.getSignature().toShortString(), executionTime);
            }
        }
    }
}
