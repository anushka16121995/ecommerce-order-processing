package com.example.ecommerce.kafka;

import com.example.ecommerce.vector.ProductEmbeddingSyncService;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class ProductEmbeddingConsumer {

    private final ProductEmbeddingSyncService productEmbeddingSyncService;

    public ProductEmbeddingConsumer(
            ProductEmbeddingSyncService productEmbeddingSyncService
    ) {
        this.productEmbeddingSyncService =
                productEmbeddingSyncService;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = "ecommerce-product-embedding",
            groupId = "product-embedding-group"
    )
    public void consume(ProductEmbeddingEvent event) {

        System.out.println(
                "ProductEmbeddingEvent received!"
        );

        System.out.println(
                "Product ID: " + event.productId()
        );

        System.out.println(
                "Action: " + event.action()
        );

        if (event.action()
                == ProductEmbeddingAction.UPSERT) {

            productEmbeddingSyncService.syncProduct(
                    event.productId()
            );
        }

        if (event.action()
                == ProductEmbeddingAction.DELETE) {

            productEmbeddingSyncService
                    .deleteProductEmbedding(
                            event.productId()
                    );
        }
    }


    @DltHandler
    public void handleDeadLetter(
            ProductEmbeddingEvent event
    ) {

        System.out.println(
                "================================="
        );

        System.out.println(
                "PRODUCT EMBEDDING EVENT FAILED"
        );

        System.out.println(
                "Product ID: " + event.productId()
        );

        System.out.println(
                "Action: " + event.action()
        );

        System.out.println(
                "Moved to Dead Letter Topic"
        );

        System.out.println(
                "================================="
        );
    }
}