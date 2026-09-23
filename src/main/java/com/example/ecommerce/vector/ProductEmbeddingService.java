package com.example.ecommerce.vector;

import com.example.ecommerce.vector.repository.ProductEmbeddingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductEmbeddingService {

    private final ProductEmbeddingRepository productEmbeddingRepository;

    public ProductEmbeddingService(
            ProductEmbeddingRepository productEmbeddingRepository
    ) {
        this.productEmbeddingRepository = productEmbeddingRepository;
    }

    public List<ProductEmbeddingView> getAllEmbeddings() {

        return productEmbeddingRepository.findAllEmbeddingViews();
    }


    public List<SimilarProductView> getSimilarProducts(
            Long productId,
            int limit
    ) {

        return productEmbeddingRepository.findSimilarProducts(
                productId,
                limit
        );
    }
}