package com.example.ecommerce.fulfillment;

import com.example.ecommerce.event.OrderCreatedEvent;
import org.springframework.stereotype.Service;

@Service
public class FulfillmentService {

    public void startFulfillment(OrderCreatedEvent event) {

        System.out.println("=================================");
        System.out.println("FULFILLMENT PROCESSING STARTED");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("User ID: " + event.getUserId());
        System.out.println("Order Total: $" + event.getTotalAmount());
        System.out.println("Status: " + event.getStatus());
        System.out.println("Preparing order for fulfillment...");
        System.out.println("=================================");
    }
}