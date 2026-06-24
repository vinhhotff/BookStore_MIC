package com.example.bookstore.order.adapter.in.web;

import com.example.bookstore.common.ApiResponse;
import com.example.bookstore.order.application.dto.OrderRequest;
import com.example.bookstore.order.application.dto.OrderResponse;
import com.example.bookstore.order.application.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
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

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<java.util.List<OrderResponse>> getMyOrders(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-User-Roles", defaultValue = "") String roles) {

        java.util.List<OrderResponse> responses = orderService.getOrderHistory(userId, roles);
        return ApiResponse.<java.util.List<OrderResponse>>builder()
                .message("Lấy lịch sử mua hàng thành công")
                .result(responses)
                .build();
    }
}
