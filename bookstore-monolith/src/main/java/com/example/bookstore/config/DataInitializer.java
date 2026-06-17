package com.example.bookstore.config;

import com.example.bookstore.book.Book;
import com.example.bookstore.category.Category;
import com.example.bookstore.role.Role;
import com.example.bookstore.user.User;
import com.example.bookstore.book.BookRepository;
import com.example.bookstore.category.CategoryRepository;
import com.example.bookstore.role.RoleRepository;
import com.example.bookstore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Set;

/**
 * DataInitializer automatically populates the database with default roles,
 * administrative users, and a curated set of sample book categories and titles
 * upon system startup if the database is detected to be empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;
    private final CacheManager cacheManager;

    @Override
    public void run(String... args) throws Exception {
        log.info("--- Bắt đầu quy trình kiểm tra và khởi tạo dữ liệu mặc định ---");

        // Xóa sạch cache khi khởi động để tránh cache cũ làm mất dữ liệu hiển thị
        if (cacheManager != null) {
            log.info("Tiến hành xóa sạch các cache chính trên Redis/Bộ nhớ tạm...");
            List.of("books", "book_detail", "categories", "category").forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                    log.info("Đã xóa cache: {}", cacheName);
                }
            });
        }

        // 1. Khởi tạo các Vai trò (Roles) mặc định
        Role adminRole = roleRepository.findById("ADMIN").orElseGet(() -> {
            Role role = Role.builder()
                    .name("ADMIN")
                    .description("Administrator with full control")
                    .build();
            log.info("Tạo mới vai trò ADMIN");
            return roleRepository.save(role);
        });

        Role userRole = roleRepository.findById("USER").orElseGet(() -> {
            Role role = Role.builder()
                    .name("USER")
                    .description("Standard user role")
                    .build();
            log.info("Tạo mới vai trò USER");
            return roleRepository.save(role);
        });

        // 2. Khởi tạo tài khoản Admin mặc định
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@bookstore.com")
                    .password(passwordEncoder.encode("123123"))
                    .firstName("Quản Trị")
                    .lastName("Viên")
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
            log.info("Đã tạo tài khoản quản trị mặc định: admin@bookstore.com / 123123");
        }

        // 3. Khởi tạo tài khoản Test User mặc định
        if (!userRepository.existsByUsername("testuser")) {
            User testUser = User.builder()
                    .username("testuser")
                    .email("testuser@bookstore.com")
                    .password(passwordEncoder.encode("123123"))
                    .firstName("Khách")
                    .lastName("Hàng")
                    .roles(Set.of(userRole))
                    .build();
            userRepository.save(testUser);
            log.info("Đã tạo tài khoản khách hàng mặc định: testuser@bookstore.com / 123123");
        }

        // 4. Khởi tạo danh sách các thể loại và sách mẫu
        if (bookRepository.count() == 0) {
            log.info("Không phát hiện sách trong database. Tiến hành tạo dữ liệu sách mẫu...");

            // Thể loại 1: Công nghệ thông tin
            Category itCat = categoryRepository.findByName("Công nghệ thông tin").orElseGet(() -> {
                Category cat = new Category();
                cat.setName("Công nghệ thông tin");
                return categoryRepository.save(cat);
            });

            Book cleanCode = new Book(null, "Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin", 150000, itCat);
            cleanCode.setStock(15);
            Book designPatterns = new Book(null, "Design Patterns: Elements of Reusable Object-Oriented Software", "Erich Gamma", 220000, itCat);
            designPatterns.setStock(8);
            Book springBoot = new Book(null, "Spring Boot in Action", "Craig Walls", 180000, itCat);
            springBoot.setStock(12);
            bookRepository.saveAll(List.of(cleanCode, designPatterns, springBoot));

            // Thể loại 2: Phát triển cá nhân
            Category selfHelpCat = categoryRepository.findByName("Phát triển cá nhân").orElseGet(() -> {
                Category cat = new Category();
                cat.setName("Phát triển cá nhân");
                return categoryRepository.save(cat);
            });

            Book dacNhanTam = new Book(null, "Đắc Nhân Tâm (How to Win Friends and Influence People)", "Dale Carnegie", 86000, selfHelpCat);
            dacNhanTam.setStock(20);
            Book nghiGiauLamGiau = new Book(null, "Nghĩ Giàu và Làm Giàu (Think and Grow Rich)", "Napoleon Hill", 95000, selfHelpCat);
            nghiGiauLamGiau.setStock(25);
            Book nhaGiaKim = new Book(null, "Nhà Giả Kim (The Alchemist)", "Paulo Coelho", 79000, selfHelpCat);
            nhaGiaKim.setStock(30);
            bookRepository.saveAll(List.of(dacNhanTam, nghiGiauLamGiau, nhaGiaKim));

            // Thể loại 3: Kinh tế & Quản trị
            Category businessCat = categoryRepository.findByName("Kinh tế & Quản trị").orElseGet(() -> {
                Category cat = new Category();
                cat.setName("Kinh tế & Quản trị");
                return categoryRepository.save(cat);
            });

            Book fastSlow = new Book(null, "Tư Duy Nhanh Và Chậm (Thinking, Fast and Slow)", "Daniel Kahneman", 145000, businessCat);
            fastSlow.setStock(10);
            Book zeroToOne = new Book(null, "Không Đến Một (Zero to One)", "Peter Thiel", 115000, businessCat);
            zeroToOne.setStock(15);
            bookRepository.saveAll(List.of(fastSlow, zeroToOne));

            log.info("Đã khởi tạo thành công 3 thể loại và 8 cuốn sách mẫu!");
        }

        log.info("--- Hoàn thành quy trình khởi tạo dữ liệu mặc định ---");
    }
}
