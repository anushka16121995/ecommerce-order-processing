package com.example.ecommerce.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    @Query("""
            SELECT DISTINCT oi.product.id
            FROM Order o
            JOIN o.items oi
            WHERE o.user.id = :userId
            """)
    List<Long> findPurchasedProductIdsByUserId(
            @Param("userId") Long userId
    );
}