package com.example.bookstore.exception;

import com.example.bookstore.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.dao.ConcurrencyFailureException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiResponse apiResponse = ApiResponse.builder().code(ErrorCode.INVALID_KEY.getCode()).message(message).build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse apiResponse = ApiResponse.builder().code(errorCode.getCode()).message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // Bắt lỗi xung đột đồng thời khi cập nhật phiên bản dữ liệu (Optimistic Locking)
    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, ConcurrencyFailureException.class})
    public ResponseEntity<ApiResponse> handleConcurrencyConflict(Exception exception) {
        log.warn("Concurrency conflict / Optimistic locking failure: {}", exception.getMessage());
        ErrorCode errorCode = ErrorCode.CONCURRENCY_CONFLICT;
        ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // Bắt toàn bộ các lỗi ngoại lệ không lường trước được (Ví dụ: NullPointerException)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse> handlingRuntimeException(Exception exception) {
        // Handle AccessDeniedException dynamically to avoid NoClassDefFoundError in services without Spring Security
        if (exception.getClass().getName().equals("org.springframework.security.access.AccessDeniedException")) {
            ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
            ApiResponse apiResponse = ApiResponse.builder()
                    .code(errorCode.getCode())
                    .message(errorCode.getMessage())
                    .build();
            return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
        }

        // Tốt nhất nên in log lỗi này ra để dev fix, không trả về cho user
        log.error("Unhandled runtime exception occurred: ", exception); 
        
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }
}
