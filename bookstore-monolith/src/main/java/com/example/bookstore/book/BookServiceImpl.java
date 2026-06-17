package com.example.bookstore.book;

import com.example.bookstore.category.Category;

import com.example.bookstore.book.BookRequest;
import com.example.bookstore.book.BookResponse;
import com.example.bookstore.exception.AppException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.book.BookMapper;
import com.example.bookstore.book.Book;
import com.example.bookstore.book.BookRepository;
import com.example.bookstore.category.CategoryRepository;
import com.example.bookstore.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {


    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Override
    @Cacheable(value = "books", key = "'all'")
    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::toBookResponse).toList();
    }

    @Override
    @Cacheable(value = "book_detail", key = "#id")
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.BOOK_NOT_EXISTED));
        return bookMapper.toBookResponse(book);
    }

    @Override
    @CacheEvict(value = {"books", "book_detail"}, allEntries = true)
    public BookResponse saveBook(BookRequest request) {
        Book book = bookMapper.toBook(request);
        return bookMapper.toBookResponse(bookRepository.save(book));
    }

    @Override
    @CacheEvict(value = {"books", "book_detail"}, allEntries = true)
    public BookResponse updateBook(Long id, BookRequest request) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        // Dùng mapper để cập nhật dữ liệu từ request vào existingBook
        // (Hoặc set thủ công nếu chưa cấu hình update mapping)
        existingBook.setTitle(request.title());
        existingBook.setAuthor(request.author());
        if (request.price() != null) {
            existingBook.setPrice(request.price());
        }
        if (request.stock() != null) {
            existingBook.setStock(request.stock());
        }

        return bookMapper.toBookResponse(bookRepository.save(existingBook));
    }

    @Override
    @CacheEvict(value = {"books", "book_detail"}, allEntries = true)
    public void deleteBook(Long id) {
        if(!bookRepository.existsById(id)) {
            throw new AppException(ErrorCode.BOOK_NOT_EXISTED);
        }
        bookRepository.deleteById(id);
    }

    @Override
    @CacheEvict(value = {"books", "book_detail"}, allEntries = true)
    public BookResponse addBooktoCategory(Long categoryId, BookRequest request) {
        return categoryRepository.findById(categoryId).map(category -> {
            Book book = bookMapper.toBook(request);
            book.setCategory(category);
            return bookMapper.toBookResponse(bookRepository.save(book));
        }).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Override
    @Cacheable(value = "books", key = "'category_' + #categoryId")
    public List<BookResponse> getBooksByCategory(Long categoryId) {
        if(!categoryRepository.existsById(categoryId)){
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND); // Nên dùng lỗi Category
        }
        List<Book> books = bookRepository.findByCategoryId(categoryId);
        // Dùng máy biến hình (Mapper) để chuyển sang DTO
        return books.stream().map(bookMapper::toBookResponse).toList();
    }

    @Override
    @Cacheable(value = "books", key = "'title_' + (#title != null ? #title : 'all')")
    public List<BookResponse> getBooksByTitle(String title) {
        List<Book> books;
        if (title != null && !title.isEmpty()) {
            books = bookRepository.findByTitleContainingIgnoreCase(title);
        } else {
            books = bookRepository.findAll();
        }
        // Chuyển List<Book> -> List<BookResponse>
        return books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
    }
    @Override
    @Cacheable(value = "books", key = "'page_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<BookResponse> getAllBooksPaged(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);

        // Dùng hàm map của Page để chuyển Entity sang DTO
        return bookPage.map(bookMapper::toBookResponse);
    }


}
