package com.example.bookstore.order.infrastructure.feign;

import com.example.bookstore.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "book-service", path = "/books")
public interface BookClient {

    @GetMapping("/{id}")
    ApiResponse<BookResponse> getBookById(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Roles") String roles);
}
