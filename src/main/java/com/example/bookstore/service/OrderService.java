package com.example.bookstore.service;

import com.example.bookstore.dto.response.OrderResponse;

import java.util.List;

/**
 * Service interface defining operations for managing Orders.
 */
public interface OrderService {

    /**
     * Places a new order for a book.
     *
     * @param bookId   the ID of the book to purchase.
     * @param quantity the quantity of the book to buy.
     */
    void placeOrder(Long bookId, int quantity);

    /**
     * Retrieves all orders for the currently authenticated user.
     *
     * @return a list of OrderResponse DTOs.
     */
    List<OrderResponse> getMyOrders();

    /**
     * Retrieves all orders in the bookstore system.
     *
     * @return a list of OrderResponse DTOs.
     */
    List<OrderResponse> getAllOrders();
}
