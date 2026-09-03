package com.example.ecommerce.notification;

import com.example.ecommerce.event.OrderCreatedEvent;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendOrderConfirmation(OrderCreatedEvent event) {

        System.out.println("=================================");
        System.out.println("ORDER CONFIRMATION NOTIFICATION");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("User ID: " + event.getUserId());
        System.out.println("Order Total: $" + event.getTotalAmount());
        System.out.println("Status: " + event.getStatus());
        System.out.println("Notification sent successfully!");
        System.out.println("=================================");
    }
}