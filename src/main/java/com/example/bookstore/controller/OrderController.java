package com.example.bookstore.controller;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.request.OrderRequest;
import com.example.bookstore.dto.response.OrderResponse;
import com.example.bookstore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Order transactions and history.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Places a new order.
     *
     * @param request the order placement details.
     * @return an empty ApiResponse with success message.
     */
    @PostMapping
    public ApiResponse<Void> placeOrder(@Valid @RequestBody OrderRequest request) {
        orderService.placeOrder(request.getBookId(), request.getQuantity());
        return ApiResponse.<Void>builder()
                .message("Đặt hàng thành công!")
                .build();
    }

    /**
     * Retrieves the order history for the currently logged-in user.
     *
     * @return an ApiResponse containing the list of OrderResponse DTOs.
     */
    @GetMapping("/my-orders")
    public ApiResponse<List<OrderResponse>> getMyOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getMyOrders())
                .build();
    }

    /**
     * Retrieves all orders in the bookstore system.
     * Restricted to ADMIN role.
     *
     * @return an ApiResponse containing the list of all OrderResponse DTOs.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getAllOrders())
                .build();
    }
}
