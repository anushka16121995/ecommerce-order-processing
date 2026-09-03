package com.example.ecommerce.kafka;

import com.example.ecommerce.event.OrderCreatedEvent;
import com.example.ecommerce.notification.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderNotificationConsumer {

    private final NotificationService notificationService;

    public OrderNotificationConsumer(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "ecommerce-order-created",
            groupId = "ecommerce-notification-group"
    )
    public void handleOrderCreated(OrderCreatedEvent event) {

        notificationService.sendOrderConfirmation(event);
    }
}