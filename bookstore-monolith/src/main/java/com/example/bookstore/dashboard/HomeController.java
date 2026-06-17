package com.example.bookstore.dashboard;

import com.example.bookstore.book.Book;

import com.example.bookstore.book.BookResponse;
import com.example.bookstore.category.CategoryResponse;
import com.example.bookstore.order.OrderResponse;
import com.example.bookstore.book.BookService;
import com.example.bookstore.category.CategoryService;
import com.example.bookstore.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * MVC Controller handling dashboard view at root path.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    /**
     * Renders the Book Rental & Subscription Platform Dashboard.
     *
     * @param model the UI Model to pass data to the HTML template.
     * @return the name of the Thymeleaf template (landing.html).
     */
    @GetMapping("/")
    public String landingPage(Model model) {
        List<BookResponse> books = bookService.getAllBooks();
        List<CategoryResponse> categories = categoryService.findAll();
        
        model.addAttribute("books", books);
        model.addAttribute("categories", categories);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                List<OrderResponse> orders = orderService.getMyOrders();
                model.addAttribute("orders", orders);
            } catch (Exception e) {
                // Log and ignore to prevent failure for unseeded test users
            }
        }
        
        return "landing";
    }
}
