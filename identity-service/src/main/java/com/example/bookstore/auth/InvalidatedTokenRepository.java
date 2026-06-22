package com.example.bookstore.auth;

import com.example.bookstore.auth.InvalidatedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository extends CrudRepository<InvalidatedToken, String> {
}
