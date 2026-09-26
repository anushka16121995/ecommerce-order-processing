package com.example.ecommerce.product;

import com.example.ecommerce.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.ecommerce.exception.ResourceNotFoundException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;


    @Test
    void shouldReturn400WhenProductRequestIsInvalid()
            throws Exception {

        ProductRequest request = new ProductRequest();

        request.setName("");
        request.setDescription("");
        request.setPrice(new BigDecimal("-10.00"));
        request.setStockQuantity(-5);


        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status").value(400)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Validation failed")
                )
                .andExpect(
                        jsonPath("$.errors.name")
                                .value("Product name is required")
                )
                .andExpect(
                        jsonPath("$.errors.price")
                                .value(
                                        "Product price must be greater than 0"
                                )
                );
    }

    @Test
    void shouldCreateProductSuccessfully()
            throws Exception {

        ProductRequest request = new ProductRequest();

        request.setName("Wireless Mouse");
        request.setDescription("Bluetooth wireless mouse");
        request.setPrice(new BigDecimal("29.99"));
        request.setStockQuantity(10);


        ProductResponse response =
                new ProductResponse(
                        1L,
                        "Wireless Mouse",
                        "Bluetooth wireless mouse",
                        new BigDecimal("29.99"),
                        10
                );


        when(
                productService.createProduct(
                        any(ProductRequest.class)
                )
        ).thenReturn(response);


        mockMvc.perform(
                        post("/api/products")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id").value(1)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Wireless Mouse")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(29.99)
                )
                .andExpect(
                        jsonPath("$.stockQuantity")
                                .value(10)
                );
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist()
            throws Exception {

        when(
                productService.getProductById(
                        anyLong()
                )
        ).thenThrow(
                new ResourceNotFoundException(
                        "Product not found with id: 999999"
                )
        );


        mockMvc.perform(
                        get("/api/products/999999")
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Product not found with id: 999999"
                                )
                );
    }
}