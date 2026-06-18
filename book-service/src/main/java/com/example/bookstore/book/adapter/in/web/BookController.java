package com.example.bookstore.book.adapter.in.web;

import com.example.bookstore.common.ApiResponse;
import com.example.bookstore.book.domain.model.Book;
import com.example.bookstore.book.domain.port.in.ManageBookUseCase;
import com.example.bookstore.exception.AppException;
import com.example.bookstore.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final ManageBookUseCase manageBookUseCase;

    @GetMapping
    public ApiResponse<List<BookResponse>> getBooks() {
        List<BookResponse> responses = manageBookUseCase.getAllBooks().stream()
                .map(this::toResponse)
                .toList();

        return ApiResponse.<List<BookResponse>>builder()
                .result(responses)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BookResponse> getBook(@PathVariable Long id) {
        Book book = manageBookUseCase.getBook(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        return ApiResponse.<BookResponse>builder()
                .result(toResponse(book))
                .build();
    }

    @PostMapping
    public ApiResponse<BookResponse> addBook(@Valid @RequestBody BookRequest request) {
        Book created = manageBookUseCase.createBook(
                request.title(),
                request.author(),
                request.price(),
                request.stock(),
                request.categoryId()
        );

        return ApiResponse.<BookResponse>builder()
                .message("Tạo sách thành công")
                .result(toResponse(created))
                .build();
    }

    @PutMapping("/{id}/stock")
    public ApiResponse<BookResponse> updateStock(@PathVariable Long id, @RequestParam int quantity) {
        Book updated = manageBookUseCase.updateStock(id, quantity);
        return ApiResponse.<BookResponse>builder()
                .message("Cập nhật kho thành công")
                .result(toResponse(updated))
                .build();
    }

    private BookResponse toResponse(Book domain) {
        if (domain == null) return null;
        String categoryName = domain.category() != null ? domain.category().name() : null;
        return BookResponse.builder()
                .id(domain.id())
                .title(domain.title())
                .author(domain.author())
                .price(domain.price())
                .stock(domain.stock())
                .categoryName(categoryName)
                .build();
    }
}
