package com.example.bookstore.book.domain.port.out;

public interface ReviewPort {
    Double getAverageRating(Long bookId);
}
