package com.example.ecommerce.vector;

import com.pgvector.PGvector;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_embeddings")
public class ProductEmbedding {

    @Id
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "embedding", columnDefinition = "vector(3)")
    private PGvector embedding;

    public ProductEmbedding() {
    }

    public ProductEmbedding(
            Long id,
            String productName,
            PGvector embedding
    ) {
        this.id = id;
        this.productName = productName;
        this.embedding = embedding;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public PGvector getEmbedding() {
        return embedding;
    }

    public void setEmbedding(PGvector embedding) {
        this.embedding = embedding;
    }
}