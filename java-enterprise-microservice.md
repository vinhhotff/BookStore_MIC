# Java Enterprise Microservice — AI Coding Rules
# Version: 2025.1 | Java 21+ | Spring Boot 3.x | GraalVM Native

---

## 🏛️ KIẾN TRÚC & TRIẾT LÝ

### Nguyên tắc nền tảng
- Tuân thủ **Domain-Driven Design (DDD)**: Bounded Context, Aggregate, Entity, Value Object, Repository, Domain Service.
- Áp dụng **Hexagonal Architecture** (Ports & Adapters): domain logic KHÔNG phụ thuộc infrastructure.
- Mỗi microservice sở hữu **một Bounded Context** duy nhất — tuyệt đối không chia sẻ database giữa các service.
- Ưu tiên **immutability**: dùng `record`, `final`, `sealed`, unmodifiable collections.
- Không bao giờ để **anemic domain model** — business logic nằm trong domain objects, không trong service layer.

### Cấu trúc package chuẩn (Hexagonal)
```
com.company.{service}/
├── domain/
│   ├── model/          # Entities, Value Objects, Aggregates
│   ├── service/        # Domain Services
│   ├── event/          # Domain Events
│   ├── port/
│   │   ├── in/         # Use Case interfaces (inbound ports)
│   │   └── out/        # Repository/External interfaces (outbound ports)
│   └── exception/      # Domain Exceptions
├── application/
│   ├── usecase/        # Use Case implementations
│   └── mapper/         # Domain ↔ DTO mappers
├── adapter/
│   ├── in/
│   │   ├── web/        # REST Controllers
│   │   └── messaging/  # Kafka/RabbitMQ Consumers
│   └── out/
│       ├── persistence/ # JPA Repositories, Entities
│       ├── messaging/   # Kafka/RabbitMQ Producers
│       └── client/      # Feign/WebClient HTTP Clients
└── infrastructure/
    ├── config/          # Spring @Configuration classes
    └── security/        # Security config
```

---

## ☕ JAVA 21+ STANDARDS

### Bắt buộc dùng tính năng modern Java
java-enterprise-microservice.mdc
**Records cho Value Objects & DTOs:**
```java
// ✅ ĐÚNG — immutable value object
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount must be non-negative");
    }
    public Money add(Money other) {
        if (!this.currency.equals(other.currency))
            throw new DomainException("Currency mismatch");
        return new Money(this.amount.add(other.amount), this.currency);
    }
}

// ❌ SAI — mutable DTO class cũ
public class MoneyDto {
    private BigDecimal amount;
    private String currency;
    // getters/setters...
}
```

**Sealed Classes cho Domain Hierarchy:**
```java
public sealed interface PaymentResult
    permits PaymentResult.Success, PaymentResult.Failed, PaymentResult.Pending {

    record Success(String transactionId, Instant processedAt) implements PaymentResult {}
    record Failed(String reason, ErrorCode code) implements PaymentResult {}
    record Pending(String trackingId, Duration estimatedWait) implements PaymentResult {}
}

// Pattern matching với switch expression
String describe(PaymentResult result) {
    return switch (result) {
        case PaymentResult.Success s  -> "Paid: " + s.transactionId();
        case PaymentResult.Failed f   -> "Failed: " + f.reason();
        case PaymentResult.Pending p  -> "Pending " + p.estimatedWait().toMinutes() + "m";
    };
}
```

**Text Blocks cho SQL/JSON/Templates:**
```java
// ✅ ĐÚNG
String query = """
    SELECT o.id, o.status, c.email
    FROM orders o
    JOIN customers c ON o.customer_id = c.id
    WHERE o.status = :status
      AND o.created_at >= :since
    ORDER BY o.created_at DESC
    """;

// ❌ SAI — String concatenation
String query = "SELECT o.id, o.status " +
               "FROM orders o " +
               "JOIN customers c ON ...";
```

**Pattern Matching instanceof:**
```java
// ✅ ĐÚNG
if (event instanceof OrderPlacedEvent placed) {
    process(placed.orderId());
}

// ❌ SAI — cast cũ
if (event instanceof OrderPlacedEvent) {
    process(((OrderPlacedEvent) event).orderId());
}
```

**Virtual Threads (Java 21 Loom) — Web & IO Tasks:**
```java
// application.properties
spring.threads.virtual.enabled=true   // Bật virtual threads toàn bộ Spring MVC

// Cho Executor tùy chỉnh
@Bean
ExecutorService ioExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
}
```

**Structured Concurrency (Java 21+):**
```java
OrderDetails fetchOrderDetails(String orderId) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<Order>    order    = scope.fork(() -> orderRepo.findById(orderId));
        Future<Customer> customer = scope.fork(() -> customerClient.getById(orderId));
        Future<List<Item>> items  = scope.fork(() -> itemRepo.findByOrderId(orderId));

        scope.join().throwIfFailed();
        return new OrderDetails(order.get(), customer.get(), items.get());
    }
}
```

---

## 🌱 SPRING BOOT 3.x RULES

### Dependency Injection
```java
// ✅ ĐÚNG — constructor injection, final fields
@Service
public class OrderService implements PlaceOrderUseCase {
    private final OrderRepository orderRepository;
    private final PaymentPort paymentPort;
    private final EventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        PaymentPort paymentPort,
                        EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.paymentPort     = paymentPort;
        this.eventPublisher  = eventPublisher;
    }
}

// ❌ SAI — field injection, không testable
@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;
}
```

### Configuration Properties (Type-Safe)
```java
// ✅ ĐÚNG
@ConfigurationProperties(prefix = "app.payment")
@Validated
public record PaymentProperties(
    @NotBlank String gatewayUrl,
    @Min(1) @Max(30) int timeoutSeconds,
    @Min(1) @Max(5)  int maxRetries,
    @NotNull CircuitBreakerConfig circuitBreaker
) {
    public record CircuitBreakerConfig(
        @Min(10) int slidingWindowSize,
        @DecimalMin("0.1") @DecimalMax("1.0") double failureRateThreshold
    ) {}
}
```

### REST Controllers
```java
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Orders", description = "Order management operations")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place a new order")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "409", description = "Business rule violation")
    })
    public OrderResponse placeOrder(
            @RequestBody @Valid PlaceOrderRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return placeOrderUseCase.place(request, principal.userId());
    }
}
```

### Global Exception Handler
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleDomainException(DomainException ex, HttpServletRequest req) {
        log.warn("Domain violation [{}]: {}", req.getRequestURI(), ex.getMessage());
        var detail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        detail.setTitle("Business Rule Violation");
        detail.setProperty("errorCode", ex.getErrorCode());
        return detail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidation(ConstraintViolationException ex) {
        var violations = ex.getConstraintViolations().stream()
            .map(v -> Map.of("field", v.getPropertyPath().toString(),
                             "message", v.getMessage()))
            .toList();
        var detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Validation Failed");
        detail.setProperty("violations", violations);
        return detail;
    }
}
```

---

## ⚡ HIỆU NĂNG — PERFORMANCE RULES

### Database & JPA
```java
// ✅ PROJECTION — chỉ lấy field cần thiết
public interface OrderSummaryProjection {
    String getId();
    OrderStatus getStatus();
    BigDecimal getTotalAmount();
    Instant getCreatedAt();
}

@Query("SELECT o.id as id, o.status as status, o.totalAmount as totalAmount, " +
       "o.createdAt as createdAt FROM OrderEntity o WHERE o.customerId = :customerId")
List<OrderSummaryProjection> findSummariesByCustomerId(@Param("customerId") String customerId);

// ✅ BATCH INSERT — tránh N+1 insert
@Modifying
@Query("INSERT INTO order_items (id, order_id, product_id, quantity) " +
       "SELECT :#{#item.id}, :#{#item.orderId}, :#{#item.productId}, :#{#item.quantity} FROM dual")
void batchInsertItems(@Param("items") List<OrderItemEntity> items);

// ✅ PAGINATION bắt buộc cho list endpoints
Page<OrderSummaryProjection> findByCustomerId(
    String customerId, Pageable pageable);

// ❌ SAI — không bao giờ dùng
List<OrderEntity> findAll(); // có thể OOM với dataset lớn
```

**JPA Entity best practices:**
```java
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_customer_status", columnList = "customer_id, status"),
    @Index(name = "idx_orders_created_at",      columnList = "created_at")
})
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "customer_id", nullable = false, updatable = false)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
               fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();

    @Version
    private Long version; // Optimistic locking — bắt buộc cho Aggregates

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
```

**N+1 Prevention:**
```java
// ✅ ĐÚNG — fetch join khi cần collection
@Query("SELECT DISTINCT o FROM OrderEntity o " +
       "LEFT JOIN FETCH o.items " +
       "WHERE o.id IN :ids")
List<OrderEntity> findWithItemsByIds(@Param("ids") List<String> ids);

// ✅ @EntityGraph cho dynamic fetching
@EntityGraph(attributePaths = {"items", "items.product"})
Optional<OrderEntity> findWithItemsById(String id);
```

### Caching Strategy
```java
@Service
@CacheConfig(cacheNames = "products")
public class ProductCacheService {

    // Cache-aside pattern
    @Cacheable(key = "#id", unless = "#result == null",
               condition = "#id != null")
    public ProductDto getProduct(String id) { /* ... */ }

    @CacheEvict(key = "#product.id")
    public void updateProduct(ProductDto product) { /* ... */ }

    @Caching(evict = {
        @CacheEvict(cacheNames = "products",   key = "#id"),
        @CacheEvict(cacheNames = "categories", allEntries = true)
    })
    public void deleteProduct(String id) { /* ... */ }
}

// Redis cache config với TTL phân cấp
@Bean
RedisCacheManagerBuilderCustomizer cacheCustomizer() {
    return builder -> builder
        .withCacheConfiguration("products",
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues()
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer())))
        .withCacheConfiguration("user-sessions",
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2)));
}
```

### Reactive & Non-Blocking (WebFlux khi cần)
```java
// Dùng WebFlux cho: streaming, high-concurrency IO, SSE
@RestController
@RequestMapping("/api/v1/events")
public class EventStreamController {

    private final EventService eventService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<OrderEvent>> streamEvents(
            @RequestParam String customerId) {
        return eventService.subscribeToCustomerEvents(customerId)
            .map(event -> ServerSentEvent.<OrderEvent>builder()
                .id(event.eventId())
                .event(event.type())
                .data(event)
                .build())
            .timeout(Duration.ofMinutes(30))
            .doOnError(e -> log.warn("Stream error for {}: {}", customerId, e.getMessage()));
    }
}

// WebClient — non-blocking HTTP calls
@Bean
WebClient paymentWebClient(WebClient.Builder builder,
                           PaymentProperties props) {
    return builder
        .baseUrl(props.gatewayUrl())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .filter(ExchangeFilterFunctions.retry(
            RetrySpec.backoff(props.maxRetries(), Duration.ofMillis(200))
                     .filter(ex -> ex instanceof WebClientResponseException wre
                                   && wre.getStatusCode().is5xxServerError())))
        .build();
}
```

---

## 🔒 SECURITY RULES

### JWT & OAuth2 Resource Server
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter())))
            .build();
    }

    @Bean
    JwtAuthenticationConverter jwtConverter() {
        var converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("roles");
        converter.setAuthorityPrefix("ROLE_");
        var jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }
}

// Method-level security
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.name")
public OrderResponse getOrder(String orderId, String userId) { /* ... */ }
```

### Input Validation
```java
// Custom validator
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = SafeStringValidator.class)
public @interface SafeString {
    String message() default "Input contains invalid characters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int maxLength() default 500;
}

public class SafeStringValidator implements ConstraintValidator<SafeString, String> {
    private static final Pattern UNSAFE = Pattern.compile("[<>\"'&;\\\\]");
    private int maxLength;

    @Override
    public void initialize(SafeString annotation) {
        this.maxLength = annotation.maxLength();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) return true;
        return value.length() <= maxLength && !UNSAFE.matcher(value).find();
    }
}
```

---

## 📨 MESSAGING — EVENT-DRIVEN

### Domain Events (Transactional Outbox Pattern)
```java
// Domain Event
public record OrderPlacedEvent(
    String eventId,
    String orderId,
    String customerId,
    Money totalAmount,
    Instant occurredAt
) implements DomainEvent {
    public static OrderPlacedEvent of(Order order) {
        return new OrderPlacedEvent(
            UUID.randomUUID().toString(),
            order.getId().value(),
            order.getCustomerId().value(),
            order.getTotalAmount(),
            Instant.now()
        );
    }
}

// Outbox Entity
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id private String id;
    @Column(nullable = false) private String aggregateType;
    @Column(nullable = false) private String aggregateId;
    @Column(nullable = false) private String eventType;
    @Column(columnDefinition = "TEXT") private String payload;
    @Column(nullable = false) private Instant createdAt;
    @Enumerated(EnumType.STRING) private OutboxStatus status;
    private Instant processedAt;
}

// Service — atomic save + outbox
@Transactional
public Order placeOrder(PlaceOrderCommand cmd) {
    var order = Order.create(cmd);
    orderRepository.save(order);

    var event = OrderPlacedEvent.of(order);
    outboxRepository.save(OutboxEvent.from(event));  // same transaction

    return order;
}

// Kafka Outbox Relay (scheduled)
@Scheduled(fixedDelay = 1000)
@Transactional
public void relayOutboxEvents() {
    outboxRepository.findPendingEvents(PageRequest.of(0, 100))
        .forEach(event -> {
            kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload())
                .whenComplete((result, ex) -> {
                    if (ex == null) markProcessed(event);
                    else log.error("Failed to relay event {}: {}", event.getId(), ex.getMessage());
                });
        });
}
```

### Kafka Consumer — Idempotency
```java
@KafkaListener(
    topics = "${app.kafka.topics.order-events}",
    groupId = "${spring.kafka.consumer.group-id}",
    containerFactory = "kafkaListenerContainerFactory"
)
@Transactional
public void handleOrderEvent(
        @Payload String payload,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.OFFSET) long offset,
        Acknowledgment ack) {
    try {
        var event = objectMapper.readValue(payload, OrderPlacedEvent.class);

        // Idempotency check
        if (processedEventRepository.existsById(event.eventId())) {
            log.info("Duplicate event {} — skipping", event.eventId());
            ack.acknowledge();
            return;
        }

        processEvent(event);
        processedEventRepository.save(new ProcessedEvent(event.eventId(), Instant.now()));
        ack.acknowledge();

    } catch (Exception ex) {
        log.error("Failed to process event at offset {}: {}", offset, ex.getMessage());
        // Do NOT ack — will retry or go to DLT
        throw new RuntimeException(ex);
    }
}
```

---

## 🔭 OBSERVABILITY

### Structured Logging (Logback + MDC)
```java
// Log request context propagation
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String traceId = Optional.ofNullable(request.getHeader("X-Trace-Id"))
            .orElse(UUID.randomUUID().toString());
        MDC.put("traceId",   traceId);
        MDC.put("userId",    extractUserId(request));
        MDC.put("service",   "${spring.application.name}");
        MDC.put("requestId", UUID.randomUUID().toString());
        try {
            response.setHeader("X-Trace-Id", traceId);
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}

// logback-spring.xml — JSON format cho ELK/Grafana Loki
// Use logstash-logback-encoder for JSON output
```

### Micrometer Metrics
```java
@Service
public class OrderMetricsService {
    private final Counter orderPlacedCounter;
    private final Timer   orderProcessingTimer;
    private final Gauge   pendingOrdersGauge;

    public OrderMetricsService(MeterRegistry registry,
                               OrderRepository orderRepository) {
        this.orderPlacedCounter = Counter.builder("orders.placed")
            .description("Total orders placed")
            .tag("env", "${spring.profiles.active}")
            .register(registry);

        this.orderProcessingTimer = Timer.builder("orders.processing.duration")
            .description("Order processing time")
            .percentiles(0.5, 0.95, 0.99)
            .sla(Duration.ofMillis(100), Duration.ofMillis(500), Duration.ofSeconds(1))
            .register(registry);

        Gauge.builder("orders.pending.count", orderRepository, r ->
            r.countByStatus(OrderStatus.PENDING))
            .description("Current pending orders")
            .register(registry);
    }

    public void recordOrderPlaced() { orderPlacedCounter.increment(); }

    public <T> T timeOrderProcessing(Supplier<T> supplier) {
        return orderProcessingTimer.record(supplier);
    }
}
```

### Health Indicators
```java
@Component
public class ExternalPaymentHealthIndicator implements HealthIndicator {
    private final PaymentGatewayClient client;

    @Override
    public Health health() {
        try {
            var status = client.ping();
            return Health.up()
                .withDetail("gateway", status.endpoint())
                .withDetail("responseTime", status.latencyMs() + "ms")
                .build();
        } catch (Exception ex) {
            return Health.down()
                .withDetail("error", ex.getMessage())
                .build();
        }
    }
}
```

---

## 🛡️ RESILIENCE — FAULT TOLERANCE

### Resilience4j — Circuit Breaker + Retry + Bulkhead
```java
// application.yml
resilience4j:
  circuitbreaker:
    instances:
      payment-service:
        slidingWindowSize: 20
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
        permittedNumberOfCallsInHalfOpenState: 5
        recordExceptions:
          - java.io.IOException
          - java.net.ConnectException
          - feign.FeignException.ServiceUnavailable

  retry:
    instances:
      payment-service:
        maxAttempts: 3
        waitDuration: 200ms
        exponentialBackoffMultiplier: 2
        retryExceptions:
          - java.io.IOException

  bulkhead:
    instances:
      payment-service:
        maxConcurrentCalls: 25
        maxWaitDuration: 500ms

  timelimiter:
    instances:
      payment-service:
        timeoutDuration: 3s
```

```java
// Usage
@CircuitBreaker(name = "payment-service", fallbackMethod = "paymentFallback")
@Retry(name = "payment-service")
@Bulkhead(name = "payment-service")
@TimeLimiter(name = "payment-service")
public CompletableFuture<PaymentResult> processPayment(PaymentCommand cmd) {
    return CompletableFuture.supplyAsync(() -> paymentClient.charge(cmd));
}

public CompletableFuture<PaymentResult> paymentFallback(
        PaymentCommand cmd, Exception ex) {
    log.warn("Payment CB open for order {}: {}", cmd.orderId(), ex.getMessage());
    return CompletableFuture.completedFuture(
        new PaymentResult.Pending(cmd.orderId(), Duration.ofMinutes(5)));
}
```

---

## 🧪 TESTING RULES

### Test Pyramid
```
         /\
        /E2E\          (ít nhất — Testcontainers, Selenium)
       /------\
      / Integration \  (vừa — Spring Boot Test, @DataJpaTest)
     /---------------\
    /    Unit Tests    \ (nhiều nhất — JUnit 5, Mockito, AssertJ)
   /--------------------\
```

**Unit Test — Domain Logic:**
```java
@ExtendWith(MockitoExtension.class)
class OrderTest {

    @Test
    @DisplayName("Should calculate correct total with discount")
    void shouldCalculateTotalWithDiscount() {
        // GIVEN
        var order = OrderFixture.anOrder()
            .withItem("PROD-1", 2, Money.of("50.00", "USD"))
            .withItem("PROD-2", 1, Money.of("30.00", "USD"))
            .build();

        // WHEN
        order.applyDiscount(DiscountCode.of("SAVE10"));

        // THEN
        assertThat(order.getTotalAmount())
            .isEqualTo(Money.of("117.00", "USD"));
        assertThat(order.getDomainEvents())
            .hasSize(1)
            .first().isInstanceOf(DiscountAppliedEvent.class);
    }
}
```

**Integration Test — Persistence:**
```java
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class OrderRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired OrderRepository orderRepository;

    @Test
    void shouldPersistAndRetrieveOrderWithItems() {
        var order = OrderFixture.aCompleteOrder();
        var saved = orderRepository.save(order);

        var found = orderRepository.findWithItemsById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getItems()).hasSize(2);
    }
}
```

**Contract Test — API:**
```java
// Provider side (Spring Cloud Contract)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureStubRunner(
    ids = "com.company:payment-service:+:stubs:8090",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class OrderServiceContractTest { /* ... */ }
```

---

## 📦 BUILD & DEPENDENCY RULES

### Maven POM Standards
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.3.x</spring-boot.version>
    <resilience4j.version>2.2.x</resilience4j.version>
    <mapstruct.version>1.6.x</mapstruct.version>
    <testcontainers.version>1.20.x</testcontainers.version>
</properties>

<!-- Bắt buộc dùng spring-boot-dependencies BOM -->
<!-- Không khai báo version nếu đã có trong BOM -->
<!-- Tuyệt đối không dùng SNAPSHOT trong production -->
<!-- Cập nhật dependencies: chạy `mvn versions:display-dependency-updates` hàng tuần -->
```

### Gradle (nếu dùng)
```kotlin
// build.gradle.kts
plugins {
    id("org.springframework.boot") version "3.3.x"
    id("io.spring.dependency-management") version "1.1.x"
    id("java")
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

tasks.compileJava {
    options.compilerArgs.addAll(listOf("--enable-preview", "-parameters"))
}
```

---

## 🐳 CONTAINERIZATION

### Dockerfile — Multi-stage, Optimized
```dockerfile
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q

COPY src ./src
RUN ./mvnw package -DskipTests -q

# Stage 2: Extract layers
FROM eclipse-temurin:21-jdk-alpine AS layertools
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 3: Runtime (minimal)
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app
COPY --from=layertools /app/dependencies/          ./
COPY --from=layertools /app/spring-boot-loader/    ./
COPY --from=layertools /app/snapshot-dependencies/ ./
COPY --from=layertools /app/application/           ./

# JVM tuning cho container
ENV JAVA_OPTS="\
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+UseG1GC \
  -XX:+ExitOnOutOfMemoryError \
  -Djava.security.egd=file:/dev/./urandom \
  -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}"

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
```

---

## 🚫 ANTI-PATTERNS — TUYỆT ĐỐI TRÁNH

| ❌ Sai | ✅ Đúng |
|--------|---------|
| `@Autowired` field injection | Constructor injection với `final` |
| `Optional.get()` không check | `orElseThrow()` / `orElse()` |
| `System.out.println` | SLF4J Logger với MDC |
| `new Date()` / `Calendar` | `java.time` (Instant, LocalDateTime) |
| Checked exceptions propagate | Wrap thành domain/unchecked exception |
| `String` cho ID/Money | Strong types: Value Objects |
| `SELECT *` trong JPQL | Explicit fields hoặc Projection |
| `findAll()` trên large table | Pagination / Streaming |
| Synchronous REST calls | Async/Reactive / Event-driven |
| Thread.sleep() trong tests | Awaitility |
| Hardcoded secrets | Spring Cloud Config / Vault |
| Mutable shared state | Immutable + STM / Actor model |
| Business logic in Controller | Domain Service / Use Case |
| Cross-service DB join | API Composition / CQRS |
| Long-lived transactions | Saga Pattern (Choreography/Orchestration) |

---

## 📋 CODE REVIEW CHECKLIST

Trước mỗi PR, đảm bảo:

- [ ] Không có `TODO` chưa được track trong issue
- [ ] Mọi `public` method đều có unit test
- [ ] Không có sensitive data trong logs (PII, card numbers, tokens)
- [ ] Database queries đã có EXPLAIN ANALYZE với data > 10K rows
- [ ] API breaking changes đã có version mới (`/v2/`)
- [ ] Circuit breaker đã cấu hình cho mọi external call
- [ ] Idempotency key cho mọi write operation qua messaging
- [ ] OpenAPI spec đã cập nhật
- [ ] `@Transactional` readOnly=true cho read operations
- [ ] No `@SuppressWarnings` không có comment giải thích
- [ ] Secrets không hardcode, không commit vào VCS
- [ ] Container image scan pass (Trivy / Snyk)

---

*Cập nhật: 2025-Q2 | Java 21 LTS | Spring Boot 3.3 | Dành cho team Enterprise*
