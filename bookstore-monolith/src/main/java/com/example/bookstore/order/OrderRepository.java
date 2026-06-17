package com.example.bookstore.order;

import com.example.bookstore.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Order entity operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.user.id = :userId")
    List<Order> findByUserIdWithItems(@Param("userId") String userId);


    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items")
    List<Order> findAllWithItems();
}
