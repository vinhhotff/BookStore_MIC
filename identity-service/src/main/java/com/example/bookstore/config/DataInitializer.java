package com.example.bookstore.config;

import com.example.bookstore.role.Role;
import com.example.bookstore.user.User;
import com.example.bookstore.role.RoleRepository;
import com.example.bookstore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("--- Bắt đầu khởi tạo dữ liệu Roles & Users cho Identity Service ---");

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

        log.info("--- Hoàn thành khởi tạo dữ liệu Identity Service ---");
    }
}
