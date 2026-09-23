package com.example.ecommerce.vector;

import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.ProductRepository;
import com.example.ecommerce.vector.repository.ProductEmbeddingRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductEmbeddingSyncService {

    private final ProductRepository productRepository;
    private final ProductEmbeddingRepository productEmbeddingRepository;
    private final EmbeddingGenerator embeddingGenerator;


    public ProductEmbeddingSyncService(
            ProductRepository productRepository,
            ProductEmbeddingRepository productEmbeddingRepository,
            EmbeddingGenerator embeddingGenerator
    ) {
        this.productRepository = productRepository;
        this.productEmbeddingRepository = productEmbeddingRepository;
        this.embeddingGenerator = embeddingGenerator;
    }


    public void syncProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        syncOneProduct(product);
    }


    public int syncAllProducts() {

        List<Product> products = productRepository.findAll();

        for (Product product : products) {

            syncOneProduct(product);
        }

        return products.size();
    }


    private void syncOneProduct(Product product) {

        String productText =
                product.getName()
                        + ". "
                        + product.getDescription();

        String embedding =
                embeddingGenerator.generate(productText);

        productEmbeddingRepository.upsertEmbedding(
                product.getId(),
                product.getName(),
                embedding
        );
    }

    public void deleteProductEmbedding(Long productId) {

        productEmbeddingRepository.deleteByProductId(
                productId
        );
    }
}