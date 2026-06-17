package com.example.bookstore.book;

import com.example.bookstore.book.BookRequest;
import com.example.bookstore.book.BookResponse;
import org.springframework.data.domain.Page; // SỬA Ở ĐÂY: Dùng springframework
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface BookService {
     List<BookResponse> getAllBooks();
    BookResponse getBookById(Long id);
     BookResponse saveBook(BookRequest book);
     BookResponse updateBook(Long id,BookRequest bookDetail);
     void deleteBook(Long id);
     BookResponse addBooktoCategory(Long categoryId, BookRequest bookDetail);
     List<BookResponse> getBooksByCategory(Long categoryId);
     List<BookResponse> getBooksByTitle(String title);
    Page<BookResponse> getAllBooksPaged(Pageable pageable);
}
