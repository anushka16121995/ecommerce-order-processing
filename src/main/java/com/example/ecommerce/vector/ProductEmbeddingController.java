package com.example.ecommerce.vector;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vector/products")
public class ProductEmbeddingController {

    private final ProductEmbeddingService productEmbeddingService;
    private final ProductEmbeddingSyncService productEmbeddingSyncService;


    public ProductEmbeddingController(
            ProductEmbeddingService productEmbeddingService,
            ProductEmbeddingSyncService productEmbeddingSyncService
    ) {
        this.productEmbeddingService = productEmbeddingService;
        this.productEmbeddingSyncService = productEmbeddingSyncService;
    }


    @GetMapping
    public List<ProductEmbeddingView> getAllEmbeddings() {

        return productEmbeddingService.getAllEmbeddings();
    }


    @GetMapping("/{productId}/similar")
    public List<SimilarProductView> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "5") int limit
    ) {

        return productEmbeddingService.getSimilarProducts(
                productId,
                limit
        );
    }


    @PostMapping("/{productId}/sync")
    public void syncProduct(
            @PathVariable Long productId
    ) {

        productEmbeddingSyncService.syncProduct(productId);
    }


    @PostMapping("/sync-all")
    public String syncAllProducts() {

        int syncedCount =
                productEmbeddingSyncService.syncAllProducts();

        return syncedCount + " products synchronized successfully";
    }
}