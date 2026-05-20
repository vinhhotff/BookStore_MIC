package com.example.bookstore.repository;

import com.example.bookstore.model.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    
    @Transactional
    @Modifying
    @Query("DELETE FROM InvalidatedToken t WHERE t.expiryTime <= ?1")
    void deleteAllExpiredSince(Date now);
}
