package com.example.ecommerce.product;

import com.example.ecommerce.kafka.ProductEmbeddingProducer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.mockito.Mockito.times;

import com.example.ecommerce.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Test
    void shouldCreateProductAndPublishEmbeddingEvent() {

        // ARRANGE
        ProductRequest request = new ProductRequest();
        request.setName("Wireless Mouse");
        request.setDescription("Bluetooth wireless mouse");
        request.setPrice(new BigDecimal("29.99"));
        request.setStockQuantity(10);


        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Wireless Mouse");
        savedProduct.setDescription("Bluetooth wireless mouse");
        savedProduct.setPrice(new BigDecimal("29.99"));
        savedProduct.setStockQuantity(10);


        when(
                productRepository.save(
                        any(Product.class)
                )
        ).thenReturn(savedProduct);


        // ACT
        ProductResponse response =
                productService.createProduct(request);


        // ASSERT
        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Wireless Mouse",
                response.getName()
        );

        assertEquals(
                "Bluetooth wireless mouse",
                response.getDescription()
        );

        assertEquals(
                new BigDecimal("29.99"),
                response.getPrice()
        );

        assertEquals(
                10,
                response.getStockQuantity()
        );


        verify(
                productRepository
        ).save(
                any(Product.class)
        );

        verify(
                productEmbeddingProducer
        ).sendUpsertEvent(1L);
    }

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEmbeddingProducer productEmbeddingProducer;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldDeleteProductAndPublishDeleteEvent() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Wireless Mouse");


        when(
                productRepository.findById(1L)
        ).thenReturn(
                Optional.of(product)
        );


        productService.deleteProduct(1L);


        verify(
                productRepository
        ).delete(product);


        verify(
                productEmbeddingProducer
        ).sendDeleteEvent(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingMissingProduct() {

        when(
                productRepository.findById(999L)
        ).thenReturn(
                Optional.empty()
        );


        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.deleteProduct(999L)
        );


        verify(
                productRepository,
                never()
        ).delete(
                any(Product.class)
        );


        verify(
                productEmbeddingProducer,
                never()
        ).sendDeleteEvent(999L);
    }

}