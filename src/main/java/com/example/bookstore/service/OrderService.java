package com.example.bookstore.service;

public interface OrderService {
    void placeOrder(Long bookId, int quantity);
}
