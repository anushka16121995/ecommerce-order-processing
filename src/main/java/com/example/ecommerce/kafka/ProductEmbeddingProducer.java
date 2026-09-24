package com.example.ecommerce.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductEmbeddingProducer {

    private static final String TOPIC =
            "ecommerce-product-embedding";

    private final KafkaTemplate<String, ProductEmbeddingEvent> kafkaTemplate;

    public ProductEmbeddingProducer(
            KafkaTemplate<String, ProductEmbeddingEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUpsertEvent(Long productId) {

        ProductEmbeddingEvent event =
                new ProductEmbeddingEvent(
                        productId,
                        ProductEmbeddingAction.UPSERT
                );

        kafkaTemplate.send(
                TOPIC,
                String.valueOf(productId),
                event
        );
    }

    public void sendDeleteEvent(Long productId) {

        ProductEmbeddingEvent event =
                new ProductEmbeddingEvent(
                        productId,
                        ProductEmbeddingAction.DELETE
                );

        kafkaTemplate.send(
                TOPIC,
                String.valueOf(productId),
                event
        );
    }
}