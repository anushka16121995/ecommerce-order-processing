package com.example.ecommerce.kafka;

import com.example.ecommerce.event.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    @KafkaListener(
            topics = "ecommerce-order-created",
            groupId = "ecommerce-order-group"
    )
    public void consume(OrderCreatedEvent event) {

        System.out.println("=================================");
        System.out.println("OrderCreatedEvent received!");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("User ID: " + event.getUserId());
        System.out.println("Total Amount: " + event.getTotalAmount());
        System.out.println("Status: " + event.getStatus());
        System.out.println("Created At: " + event.getCreatedAt());
        System.out.println("=================================");
    }
}