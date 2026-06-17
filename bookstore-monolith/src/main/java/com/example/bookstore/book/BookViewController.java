package com.example.bookstore.book;

import com.example.bookstore.book.BookResponse;
import com.example.bookstore.category.CategoryResponse;
import com.example.bookstore.order.OrderResponse;
import com.example.bookstore.book.BookService;
import com.example.bookstore.category.CategoryService;
import com.example.bookstore.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * MVC Controller handling web page routing and rendering for Books using Thymeleaf.
 */
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookViewController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    /**
     * Renders the home page displaying categories and the list of books.
     *
     * @param model the UI Model to pass data to the HTML template.
     * @return the name of the Thymeleaf template (books/index.html).
     */
    @GetMapping
    public String showBookList(Model model) {
        List<BookResponse> books = bookService.getAllBooks();
        List<CategoryResponse> categories = categoryService.findAll();
        
        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        
        return "books/index";
    }

    /**
     * Renders the home page filtering books by a specific category.
     *
     * @param categoryId the ID of the category.
     * @param model      the UI Model to pass data to the HTML template.
     * @return the name of the Thymeleaf template (books/index.html).
     */
    @GetMapping("/categories/{categoryId}")
    public String showBookListByCategory(@PathVariable Long categoryId, Model model) {
        List<BookResponse> books = bookService.getBooksByCategory(categoryId);
        List<CategoryResponse> categories = categoryService.findAll();
        
        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        model.addAttribute("activeCategoryId", categoryId);
        
        return "books/index";
    }

    /**
     * Renders the detailed information page for a specific book.
     *
     * @param id    the unique ID of the book.
     * @param model the UI Model to pass data to the HTML template.
     * @return the name of the Thymeleaf template (books/details.html).
     */
    @GetMapping("/{id}")
    public String showBookDetails(@PathVariable Long id, Model model) {
        BookResponse book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "books/details";
    }

    /**
     * Renders the order history page for the currently logged-in user.
     *
     * @param model the UI Model to pass data to the HTML template.
     * @return the name of the Thymeleaf template (books/orders.html).
     */
    @GetMapping("/my-orders")
    public String showMyOrders(Model model) {
        List<OrderResponse> orders = orderService.getMyOrders();
        model.addAttribute("orders", orders);
        return "books/orders";
    }

    /**
     * Handles order placement requests submitted from the web interface.
     *
     * @param bookId             the ID of the book being ordered.
     * @param quantity           the quantity to order.
     * @param redirectAttributes helper to pass flash attributes to the redirected page.
     * @return redirect path to the book details page.
     */
    @PostMapping("/orders")
    public String placeOrderWeb(@RequestParam Long bookId, @RequestParam int quantity, RedirectAttributes redirectAttributes) {
        try {
            orderService.placeOrder(bookId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt mua sách thành công! Email xác nhận đang được gửi đi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đặt mua thất bại: " + e.getMessage());
        }
        return "redirect:/books/" + bookId;
    }
}
