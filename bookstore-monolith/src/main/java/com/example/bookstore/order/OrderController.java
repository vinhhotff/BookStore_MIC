package com.example.bookstore.order;

import com.example.bookstore.common.ApiResponse;
import com.example.bookstore.order.OrderRequest;
import com.example.bookstore.order.OrderResponse;
import com.example.bookstore.order.OrderService;
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


    @PostMapping
    public ApiResponse<Void> placeOrder(@Valid @RequestBody OrderRequest request) {
        orderService.placeOrder(request.bookId(), request.quantity());
        return ApiResponse.<Void>builder()
                .message("Đặt hàng thành công!")
                .build();
    }


    @GetMapping("/my-orders")
    public ApiResponse<List<OrderResponse>> getMyOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getMyOrders())
                .build();
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getAllOrders())
                .build();
    }
}
