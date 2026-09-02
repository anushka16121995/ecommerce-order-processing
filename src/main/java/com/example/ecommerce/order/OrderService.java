package com.example.ecommerce.order;

import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.ProductRepository;
import com.example.ecommerce.user.User;
import com.example.ecommerce.user.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }


    // ---------------------------------------------------------
    // CREATE ORDER
    // ---------------------------------------------------------

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        // STEP 1:
        // Find the user who is placing the order.
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + request.getUserId()
                        )
                );


        // STEP 2:
        // Create a new Order object in Java memory.
        //
        // At this moment it is NOT saved in MySQL yet.
        Order order = new Order(
                user,
                OrderStatus.CREATED,
                BigDecimal.ZERO,
                LocalDateTime.now()
        );


        // STEP 3:
        // Process every requested product in the order.
        for (OrderItemRequest itemRequest : request.getItems()) {

            // Find the actual Product from MySQL.
            Product product = productRepository
                    .findByIdForUpdate(itemRequest.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found with id: "
                                            + itemRequest.getProductId()
                            )
                    );


            // STEP 4:
            // Check whether enough stock exists.
            if (product.getStockQuantity() < itemRequest.getQuantity()) {

                throw new InsufficientStockException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }


            // STEP 5:
            // Calculate:
            //
            // product price x requested quantity
            //
            // Example:
            // 199.99 x 2 = 399.98
            BigDecimal lineTotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemRequest.getQuantity()
                                    )
                            );


            // STEP 6:
            // Reduce inventory.
            //
            // Example:
            // stock 10 - quantity 2 = stock 8
            product.setStockQuantity(
                    product.getStockQuantity()
                            - itemRequest.getQuantity()
            );


            // STEP 7:
            // Create an OrderItem representing this product
            // inside this particular order.
            OrderItem orderItem = new OrderItem(
                    order,
                    product,
                    itemRequest.getQuantity(),
                    product.getPrice()
            );


            // STEP 8:
            // Attach the OrderItem to the Order.
            order.addItem(orderItem);


            // STEP 9:
            // Add this line total to the running order total.
            order.setTotalAmount(
                    order.getTotalAmount().add(lineTotal)
            );


            // Temporary debugging output.
            System.out.println(
                    "Processed product: " + product.getName()
            );

            System.out.println(
                    "Line total: " + lineTotal
            );

            System.out.println(
                    "Current order total: " + order.getTotalAmount()
            );
        }


        // STEP 10:
        // The loop is now finished.
        //
        // That means ALL products have been checked and processed.
        //
        // Now save the complete Order.
        Order savedOrder = orderRepository.save(order);


        System.out.println(
                "Saved order with id: " + savedOrder.getId()
        );

        System.out.println(
                "Final order total: " + savedOrder.getTotalAmount()
        );


        // STEP 11:
        // Convert the saved database entity into the DTO
        // that will eventually be returned to Swagger.
        return mapToResponse(savedOrder);
    }


    // ---------------------------------------------------------
    // GET ONE ORDER
    // ---------------------------------------------------------

    public OrderResponse getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + id
                        )
                );

        return mapToResponse(order);
    }


    // ---------------------------------------------------------
    // GET ALL ORDERS
    // ---------------------------------------------------------

    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ---------------------------------------------------------
    // CONVERT ONE ORDER ITEM ENTITY INTO RESPONSE DTO
    // ---------------------------------------------------------

    private OrderItemResponse mapItemToResponse(OrderItem item) {

        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPriceAtPurchase()
        );
    }


    // ---------------------------------------------------------
    // CONVERT ONE ORDER ENTITY INTO RESPONSE DTO
    // ---------------------------------------------------------

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> itemResponses =
                order.getItems()
                        .stream()
                        .map(this::mapItemToResponse)
                        .toList();


        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}