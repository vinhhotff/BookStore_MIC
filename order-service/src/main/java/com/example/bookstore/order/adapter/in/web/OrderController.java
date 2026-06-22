package com.example.bookstore.order.adapter.in.web;

import com.example.bookstore.common.ApiResponse;
import com.example.bookstore.order.application.dto.OrderRequest;
import com.example.bookstore.order.application.dto.OrderResponse;
import com.example.bookstore.order.application.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-User-Roles", defaultValue = "") String roles) {

        OrderResponse response = orderService.placeOrder(request, userId, roles);
        return ApiResponse.<OrderResponse>builder()
                .message("Đặt hàng thành công")
                .result(response)
                .build();
    }
}
