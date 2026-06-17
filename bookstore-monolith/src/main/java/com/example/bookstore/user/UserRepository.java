package com.example.bookstore.user;

import com.example.bookstore.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByUsername (String userName);
    Optional<User>  findByEmail(String email);
}
