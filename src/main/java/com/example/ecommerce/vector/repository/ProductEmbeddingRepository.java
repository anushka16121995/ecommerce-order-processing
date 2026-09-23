package com.example.ecommerce.vector.repository;

import com.example.ecommerce.vector.ProductEmbedding;
import com.example.ecommerce.vector.ProductEmbeddingView;
import com.example.ecommerce.vector.SimilarProductView;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.vector.UserPurchasedEmbeddingView;

public interface ProductEmbeddingRepository
        extends JpaRepository<ProductEmbedding, Long> {

    @Query(
            value = """
                    SELECT
                        id AS id,
                        product_id AS "productId",
                        product_name AS "productName",
                        embedding::text AS embedding
                    FROM product_embeddings
                    ORDER BY id
                    """,
            nativeQuery = true
    )
    List<ProductEmbeddingView> findAllEmbeddingViews();


    @Query(
            value = """

                    SELECT
                    p.id AS id,
                    p.product_id AS "productId",
                    p.product_name AS "productName",
                    p.embedding::text AS embedding,
                    p.embedding <-> target.embedding AS distance
                FROM product_embeddings p
                CROSS JOIN (
                    SELECT embedding
                    FROM product_embeddings
                    WHERE product_id = :productId
                ) target
                WHERE p.product_id <> :productId
                ORDER BY p.embedding <-> target.embedding
                LIMIT :limit
                """,
            nativeQuery = true
    )
    List<SimilarProductView> findSimilarProducts(
            @Param("productId") Long productId,
            @Param("limit") int limit
    );

    @Query(
            value = """
                SELECT
                    product_id AS "productId",
                    embedding::text AS embedding
                FROM product_embeddings
                WHERE product_id IN (:productIds)
                """,
            nativeQuery = true
    )
    List<UserPurchasedEmbeddingView> findEmbeddingsByProductIds(
            @Param("productIds") List<Long> productIds
    );

    @Query(
            value = """
                SELECT
                    p.id AS id,
                    p.product_id AS "productId",
                    p.product_name AS "productName",
                    p.embedding::text AS embedding,
                    p.embedding <-> CAST(:userVector AS vector) AS distance
                FROM product_embeddings p
                WHERE p.product_id NOT IN (:excludedProductIds)
                ORDER BY p.embedding <-> CAST(:userVector AS vector)
                LIMIT :limit
                """,
            nativeQuery = true
    )
    List<SimilarProductView> findSimilarToUserVector(
            @Param("userVector") String userVector,
            @Param("excludedProductIds") List<Long> excludedProductIds,
            @Param("limit") int limit
    );

    @Modifying
    @Transactional(transactionManager = "postgresTransactionManager")
    @Query(
            value = """
                INSERT INTO product_embeddings (
                    product_id,
                    product_name,
                    embedding
                )
                VALUES (
                    :productId,
                    :productName,
                    CAST(:embedding AS vector)
                )
                ON CONFLICT (product_id)
                DO UPDATE SET
                    product_name = EXCLUDED.product_name,
                    embedding = EXCLUDED.embedding
                """,
            nativeQuery = true
    )
    void upsertEmbedding(
            @Param("productId") Long productId,
            @Param("productName") String productName,
            @Param("embedding") String embedding
    );

    @Modifying
    @Transactional(transactionManager = "postgresTransactionManager")
    @Query(
            value = """
                DELETE FROM product_embeddings
                WHERE product_id = :productId
                """,
            nativeQuery = true
    )
    void deleteByProductId(
            @Param("productId") Long productId
    );
    }