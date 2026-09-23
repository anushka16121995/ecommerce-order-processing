package com.example.ecommerce.kafka;

import com.example.ecommerce.event.OrderCreatedEvent;
import com.example.ecommerce.fulfillment.FulfillmentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderFulfillmentConsumer {

    private final FulfillmentService fulfillmentService;

    public OrderFulfillmentConsumer(
            FulfillmentService fulfillmentService
    ) {
        this.fulfillmentService = fulfillmentService;
    }

    @KafkaListener(
            topics = "ecommerce-order-created",
            groupId = "ecommerce-fulfillment-group"
    )
    public void handleOrderCreated(OrderCreatedEvent event) {

        fulfillmentService.startFulfillment(event);
    }
}