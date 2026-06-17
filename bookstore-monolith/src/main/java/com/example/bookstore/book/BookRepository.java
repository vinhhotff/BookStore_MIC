package com.example.bookstore.book;

import com.example.bookstore.book.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @EntityGraph(attributePaths = {"category"})
    List<Book> findAll();

    @EntityGraph(attributePaths = {"category"})
    List<Book> findByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {"category"})
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    @EntityGraph(attributePaths = {"category"})
    List<Book> findByAuthor(String author);
}
