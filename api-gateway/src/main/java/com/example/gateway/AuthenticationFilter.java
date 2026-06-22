package com.example.gateway;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.signerKey}")
    private String signerKey;

    private final ReactiveStringRedisTemplate redisTemplate;

    // Các đường dẫn cho phép đi qua không cần Token (Ví dụ: Login, Introspect)
    private final List<String> publicEndpoints = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/introspect",
            "/api/v1/auth/logout",
            "/api/v1/auth/refresh"
    );

    public AuthenticationFilter(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. Nếu là đường dẫn công cộng -> Cho qua luôn
        if (isPublicEndpoint(request)) {
            return chain.filter(exchange);
        }

        // 2. Lấy Token từ Header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthenticated(exchange);
        }

        String token = authHeader.substring(7);

        try {
            // 3. Giải mã và kiểm tra chữ ký (Stateless Verification)
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
            boolean verified = signedJWT.verify(verifier);

            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (!verified || expiryTime.before(new Date())) {
                return unauthenticated(exchange);
            }

            // 4. Kiểm tra xem Token có nằm trong Sổ đen (Redis) không?
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            String redisKey = "InvalidatedToken:" + jti; // Dựa theo @RedisHash của identity-service

            return redisTemplate.hasKey(redisKey).flatMap(isBlacklisted -> {
                if (isBlacklisted != null && isBlacklisted) {
                    log.warn("Token is blacklisted (Logged out)! JTI: {}", jti);
                    return unauthenticated(exchange);
                }

                // 5. Nếu an toàn, truyền userId xuống các Service bên dưới
                try {
                    String userId = signedJWT.getJWTClaimsSet().getSubject();
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } catch (Exception e) {
                    return unauthenticated(exchange);
                }
            });

        } catch (Exception e) {
            log.error("Lỗi xác thực Token: {}", e.getMessage());
            return unauthenticated(exchange);
        }
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        return publicEndpoints.stream().anyMatch(path -> request.getURI().getPath().matches(path.replace("**", ".*")));
    }

    private Mono<Void> unauthenticated(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return 0; // Chạy sau LoggingFilter (-1)
    }
}
