package com.example.bookstore.book.domain.model;

import com.example.bookstore.exception.AppException;
import com.example.bookstore.exception.ErrorCode;

public record Book(
    Long id,
    String title,
    String author,
    double price,
    int stock,
    Category category,
    Integer version,
    Double rating
) {
    public Book withStock(int newStock) {
        return new Book(id, title, author, price, newStock, category, version, rating);
    }

    public Book applyDiscount(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new AppException(
                    ErrorCode.INVALID_DISCOUNT);
        }
        double newPrice = this.price - (this.price * percentage / 100);
        return new Book(id, title, author, newPrice, stock, category, version, rating);
    }

    public Book withRating(Double newRating) {
        return new Book(id, title, author, price, stock, category, version, newRating);
    }
}
