package com.example.ecommerce.kafka;

import com.example.ecommerce.event.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final String ORDER_CREATED_TOPIC =
            "ecommerce-order-created";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;


    public OrderEventProducer(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void publishOrderCreated(OrderCreatedEvent event) {

        String key = event.getOrderId().toString();

        kafkaTemplate.send(
                ORDER_CREATED_TOPIC,
                key,
                event
        );
    }
}