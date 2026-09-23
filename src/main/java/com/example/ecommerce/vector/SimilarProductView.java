package com.example.ecommerce.vector;

public interface SimilarProductView {

    Long getId();

    Long getProductId();

    String getProductName();

    String getEmbedding();

    Double getDistance();
}