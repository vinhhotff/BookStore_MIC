package com.example.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Lấy ra hoặc tạo mới một Mã theo dõi (Correlation ID)
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
            // Gắn mã này vào Request để truyền xuống các service bên dưới (Book, Identity...)
            exchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header(CORRELATION_ID_HEADER, correlationId)
                            .build())
                    .build();
        }

        log.info("Incoming Request: {} {} | Correlation-Id: {}", 
                exchange.getRequest().getMethod(), 
                exchange.getRequest().getURI().getPath(), 
                correlationId);

        final String finalCorrelationId = correlationId;
        final ServerWebExchange finalExchange = exchange;

        // 2. Chuyển tiếp Request đi xuống các service
        return chain.filter(finalExchange).then(Mono.fromRunnable(() -> {
            // 3. Đoạn này sẽ chạy sau khi các service (Book/Identity) xử lý xong và trả về kết quả
            log.info("Outgoing Response: {} | Correlation-Id: {}", 
                    finalExchange.getResponse().getStatusCode(), 
                    finalCorrelationId);
        }));
    }

    @Override
    public int getOrder() {
        // Đặt thứ tự ưu tiên (số càng nhỏ càng chạy sớm)
        return -1;
    }
}
