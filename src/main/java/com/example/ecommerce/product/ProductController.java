package com.example.ecommerce.product;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;


    public ProductController(
            ProductService productService
    ) {
        this.productService = productService;
    }


    // CREATE PRODUCT
    @PostMapping
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request
    ) {

        return productService.createProduct(request);
    }


    // GET ALL PRODUCTS
    @GetMapping
    public List<ProductResponse> getAllProducts() {

        return productService.getAllProducts();
    }


    // GET ONE PRODUCT BY ID
    @GetMapping("/{id}")
    public ProductResponse getProductById(
            @PathVariable Long id
    ) {

        return productService.getProductById(id);
    }


    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {

        return productService.updateProduct(
                id,
                request
        );
    }


    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public void deleteProduct(
            @PathVariable Long id
    ) {

        productService.deleteProduct(id);
    }
}