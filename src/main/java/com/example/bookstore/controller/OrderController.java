package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.request.OrderRequest;
import com.example.bookstore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<Void> placeOrder(@Valid @RequestBody OrderRequest request) {
        orderService.placeOrder(request.getBookId(), request.getQuantity());
        return ApiResponse.<Void>builder()
                .message("Đặt hàng thành công!")
                .build();
    }
}
