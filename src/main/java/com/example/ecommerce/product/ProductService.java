package com.example.ecommerce.product;

import org.springframework.stereotype.Service;

import java.util.List;

import com.example.ecommerce.exception.ResourceNotFoundException;

import com.example.ecommerce.vector.ProductEmbeddingSyncService;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductEmbeddingSyncService productEmbeddingSyncService;

    public ProductService(
            ProductRepository productRepository,
            ProductEmbeddingSyncService productEmbeddingSyncService
    ) {
        this.productRepository = productRepository;
        this.productEmbeddingSyncService = productEmbeddingSyncService;
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

        productEmbeddingSyncService.syncProduct(
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

        Product updatedProduct = productRepository.save(product);

        productEmbeddingSyncService.syncProduct(
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

        productEmbeddingSyncService.deleteProductEmbedding(id);

        productRepository.delete(product);
    }
}