package com.example.ecommerce.product;

import org.springframework.stereotype.Service;

import java.util.List;

import com.example.ecommerce.exception.ResourceNotFoundException;

import com.example.ecommerce.vector.ProductEmbeddingSyncService;

import com.example.ecommerce.kafka.ProductEmbeddingProducer;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductEmbeddingProducer productEmbeddingProducer;

    public ProductService(
            ProductRepository productRepository,
            ProductEmbeddingProducer productEmbeddingProducer
    ) {
        this.productRepository = productRepository;
        this.productEmbeddingProducer =
                productEmbeddingProducer;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        return mapToResponse(product);
    }

    public ProductResponse createProduct(ProductRequest request) {

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStockQuantity()
        );

        Product savedProduct =
                productRepository.save(product);

        productEmbeddingProducer.sendUpsertEvent(
                savedProduct.getId()
        );

        return mapToResponse(savedProduct);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        Product updatedProduct =
                productRepository.save(product);

        productEmbeddingProducer.sendUpsertEvent(
                updatedProduct.getId()
        );

        return mapToResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        productRepository.delete(product);

        productEmbeddingProducer.sendDeleteEvent(id);
    }
}