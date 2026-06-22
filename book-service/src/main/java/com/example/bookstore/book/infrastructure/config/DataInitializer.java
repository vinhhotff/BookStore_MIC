package com.example.bookstore.book.infrastructure.config;

import com.example.bookstore.book.domain.model.Book;
import com.example.bookstore.book.domain.model.Category;
import com.example.bookstore.book.domain.port.out.BookRepositoryPort;
import com.example.bookstore.book.domain.port.out.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final BookRepositoryPort bookRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void run(String... args) throws Exception {
        log.info("--- Bắt đầu khởi tạo dữ liệu Catalog Sách & Thể loại cho Book Service ---");

        // 1. Khởi tạo Thể loại 1: Công nghệ thông tin
        Category itCat = categoryRepositoryPort.findByName("Công nghệ thông tin").orElseGet(() -> {
            Category cat = new Category(null, "Công nghệ thông tin");
            log.info("Tạo mới thể loại Công nghệ thông tin");
            return categoryRepositoryPort.save(cat);
        });

        // Khởi tạo Thể loại 2: Phát triển cá nhân
        Category selfHelpCat = categoryRepositoryPort.findByName("Phát triển cá nhân").orElseGet(() -> {
            Category cat = new Category(null, "Phát triển cá nhân");
            log.info("Tạo mới thể loại Phát triển cá nhân");
            return categoryRepositoryPort.save(cat);
        });

        // Khởi tạo Thể loại 3: Kinh tế & Quản trị
        Category businessCat = categoryRepositoryPort.findByName("Kinh tế & Quản trị").orElseGet(() -> {
            Category cat = new Category(null, "Kinh tế & Quản trị");
            log.info("Tạo mới thể loại Kinh tế & Quản trị");
            return categoryRepositoryPort.save(cat);
        });

        // 2. Khởi tạo Sách nếu database rỗng
        if (bookRepositoryPort.findAll().isEmpty()) {
            log.info("Không phát hiện sách trong database. Tiến hành tạo sách mẫu...");

            bookRepositoryPort.save(new Book(null, "Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin", 150000, 15, itCat, 0, 5.0));
            bookRepositoryPort.save(new Book(null, "Design Patterns: Elements of Reusable Object-Oriented Software", "Erich Gamma", 220000, 8, itCat, 0, 5.0));
            bookRepositoryPort.save(new Book(null, "Spring Boot in Action", "Craig Walls", 180000, 12, itCat, 0, 5.0));

            bookRepositoryPort.save(new Book(null, "Đắc Nhân Tâm (How to Win Friends and Influence People)", "Dale Carnegie", 86000, 20, selfHelpCat, 0, 5.0));
            bookRepositoryPort.save(new Book(null, "Nghĩ Giàu và Làm Giàu (Think and Grow Rich)", "Napoleon Hill", 95000, 25, selfHelpCat, 0, 5.0));
            bookRepositoryPort.save(new Book(null, "Nhà Giả Kim (The Alchemist)", "Paulo Coelho", 79000, 30, selfHelpCat, 0, 5.0));

            bookRepositoryPort.save(new Book(null, "Tư Duy Nhanh Và Chậm (Thinking, Fast and Slow)", "Daniel Kahneman", 145000, 10, businessCat, 0, 5.0));
            bookRepositoryPort.save(new Book(null, "Không Đến Một (Zero to One)", "Peter Thiel", 115000, 15, businessCat, 0, 5.0));

            log.info("Khởi tạo sách mẫu thành công!");
        }

        log.info("--- Hoàn thành khởi tạo dữ liệu Book Service ---");
    }
}
