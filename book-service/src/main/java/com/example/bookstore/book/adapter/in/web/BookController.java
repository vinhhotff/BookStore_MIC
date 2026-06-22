package com.example.bookstore.book.adapter.in.web;

import com.example.bookstore.book.mapper.BookMapper;
import com.example.bookstore.common.ApiResponse;
import com.example.bookstore.book.domain.model.Book;
import com.example.bookstore.book.domain.port.in.ManageBookUseCase;
import com.example.bookstore.exception.AppException;
import com.example.bookstore.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final ManageBookUseCase manageBookUseCase;
    private final BookMapper mapper;

    @GetMapping
    public ApiResponse<List<BookResponse>> getBooks() {
        List<BookResponse> responses = manageBookUseCase.getAllBooks().stream()
                .map(mapper::toResponse)
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
                .result(mapper.toResponse(book))
                .build();
    }

    @PostMapping
    public ApiResponse<BookResponse> addBook(@Valid @RequestBody BookRequest request) {
        Book bookToCreate = mapper.toDomain(request);
        Book created = manageBookUseCase.createBook(bookToCreate);

        return ApiResponse.<BookResponse>builder()
                .message("Tạo sách thành công")
                .result(mapper.toResponse(created))
                .build();
    }

    @PutMapping("/{id}/stock")
    public ApiResponse<BookResponse> updateStock(@PathVariable Long id, @RequestParam int quantity) {
        Book updated = manageBookUseCase.updateStock(id, quantity);
        return ApiResponse.<BookResponse>builder()
                .message("Cập nhật kho thành công")
                .result(mapper.toResponse(updated))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<BookResponse> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        Book updatedBook = mapper.toDomain(request);
        Book updateBook = manageBookUseCase.updateBook(id, updatedBook);
        return ApiResponse.<BookResponse>builder()
                .message("Update Book thanh cong")
                .result(mapper.toResponse(updateBook))
                .build();
    }

    @PatchMapping("/{id}/discount")
    public ApiResponse<BookResponse> applyDiscount(@PathVariable Long id, @RequestParam double percentage) {
        Book discountedBook = manageBookUseCase.applyDiscount(id, percentage);
        return ApiResponse.<BookResponse>builder()
                .message("Áp dụng mã giảm giá thành công")
                .result(mapper.toResponse(discountedBook))
                .build();
    }
}
