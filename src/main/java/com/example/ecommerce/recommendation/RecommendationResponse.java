package com.example.ecommerce.recommendation;

import java.math.BigDecimal;

public class RecommendationResponse {

    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Double distance;

    public RecommendationResponse(
            Long productId,
            String productName,
            String description,
            BigDecimal price,
            Integer stockQuantity,
            Double distance
    ) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.distance = distance;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public Double getDistance() {
        return distance;
    }
}