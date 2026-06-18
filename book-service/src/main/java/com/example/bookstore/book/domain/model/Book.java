package com.example.bookstore.book.domain.model;

public record Book(
    Long id,
    String title,
    String author,
    double price,
    int stock,
    Category category
) {
    public Book withStock(int newStock) {
        return new Book(id, title, author, price, newStock, category);
    }
}
