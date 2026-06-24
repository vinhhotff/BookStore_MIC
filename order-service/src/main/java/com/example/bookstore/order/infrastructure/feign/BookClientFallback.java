package com.example.bookstore.order.infrastructure.feign;

import com.example.bookstore.common.ApiResponse;
import com.example.bookstore.exception.AppException;
import com.example.bookstore.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookClientFallback implements BookClient {

    @Override
    public ApiResponse<BookResponse> getBookById(Long id, String userId, String roles) {
        log.error("Circuit Breaker nhảy mạch! Dịch vụ Book đang bị sập khi gọi getBookById(id={}).", id);
        // Throw exception ngay lập tức để không treo hệ thống
        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
    }
}
