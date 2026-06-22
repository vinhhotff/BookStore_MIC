package com.example.bookstore.book.adapter.out.api;

import com.example.bookstore.book.domain.port.out.ReviewPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ReviewApiAdapter implements ReviewPort {

    private final RestClient restClient;

    public ReviewApiAdapter(RestClient.Builder restClientBuilder) {
        // Cấu hình RestClient trỏ tới một API công cộng để học tập
        this.restClient = restClientBuilder
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    @Override
    public Double getAverageRating(Long bookId) {
        try {
            // 1. Thực hiện một cuộc gọi HTTP GET ra ngoài internet
            TodoDto response = restClient.get()
                    .uri("/todos/{id}", bookId)
                    .retrieve()
                    .body(TodoDto.class); // 2. Tự động parse JSON thành Object Java

            // 3. Xử lý logic giả lập rating (nếu todo completed thì rate 5.0, không thì 3.5)
            if (response != null && response.completed()) {
                return 5.0;
            }
            return 3.5;
        } catch (Exception e) {
            // 4. Nếu API ngoài bị sập hoặc lỗi mạng, trả về rating mặc định
            return 0.0;
        }
    }

    // DTO để hứng dữ liệu từ public API trả về (tương ứng với JSON của jsonplaceholder)
    record TodoDto(Long id, Long userId, String title, boolean completed) {}
}
