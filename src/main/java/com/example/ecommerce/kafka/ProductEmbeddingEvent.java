package com.example.ecommerce.kafka;

public record ProductEmbeddingEvent(
        Long productId,
        ProductEmbeddingAction action
) {
}