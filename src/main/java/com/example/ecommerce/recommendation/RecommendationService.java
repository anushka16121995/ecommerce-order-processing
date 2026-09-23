package com.example.ecommerce.recommendation;

import com.example.ecommerce.order.OrderRepository;
import com.example.ecommerce.product.Product;
import com.example.ecommerce.product.ProductRepository;
import com.example.ecommerce.vector.SimilarProductView;
import com.example.ecommerce.vector.UserPurchasedEmbeddingView;
import com.example.ecommerce.vector.repository.ProductEmbeddingRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.example.ecommerce.vector.UserPurchasedEmbeddingView;
@Service
public class RecommendationService {

    private final OrderRepository orderRepository;
    private final ProductEmbeddingRepository productEmbeddingRepository;
    private final ProductRepository productRepository;

    public RecommendationService(
            OrderRepository orderRepository,
            ProductEmbeddingRepository productEmbeddingRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.productEmbeddingRepository = productEmbeddingRepository;
        this.productRepository = productRepository;
    }


    public List<RecommendationResponse> getRecommendationsForUser(
            Long userId,
            int limit
    ) {

        // STEP 1:
        // Find products already purchased by the user.
        List<Long> purchasedProductIds =
                orderRepository.findPurchasedProductIdsByUserId(userId);


        if (purchasedProductIds.isEmpty()) {
            return List.of();
        }


        // STEP 2:
        // Fetch the embeddings of those purchased products.
        List<UserPurchasedEmbeddingView> purchasedEmbeddings =
                productEmbeddingRepository.findEmbeddingsByProductIds(
                        purchasedProductIds
                );


        if (purchasedEmbeddings.isEmpty()) {
            return List.of();
        }


        // STEP 3:
        // Calculate one average vector representing the user's taste.
        String userVector =
                calculateAverageVector(purchasedEmbeddings);


        // STEP 4:
        // Ask PostgreSQL for products closest to that user vector.
        List<SimilarProductView> similarProducts =
                productEmbeddingRepository.findSimilarToUserVector(
                        userVector,
                        purchasedProductIds,
                        limit
                );


        // STEP 5:
        // Fetch the real product details from MySQL.
        List<RecommendationResponse> recommendations =
                new ArrayList<>();


        for (SimilarProductView similarProduct : similarProducts) {

            Product product =
                    productRepository
                            .findById(similarProduct.getProductId())
                            .orElse(null);


            if (product == null) {
                continue;
            }


            RecommendationResponse response =
                    new RecommendationResponse(
                            product.getId(),
                            product.getName(),
                            product.getDescription(),
                            product.getPrice(),
                            product.getStockQuantity(),
                            similarProduct.getDistance()
                    );


            recommendations.add(response);
        }


        return recommendations;
    }


    private String calculateAverageVector(
            List<UserPurchasedEmbeddingView> purchasedEmbeddings
    ) {

        List<double[]> parsedEmbeddings =
                new ArrayList<>();


        for (UserPurchasedEmbeddingView embeddingView : purchasedEmbeddings) {

            double[] vector =
                    parseVector(
                            embeddingView.getEmbedding()
                    );

            parsedEmbeddings.add(vector);
        }


        int dimensions =
                parsedEmbeddings.get(0).length;


        double[] average =
                new double[dimensions];


        for (double[] vector : parsedEmbeddings) {

            for (int i = 0; i < dimensions; i++) {

                average[i] += vector[i];
            }
        }


        for (int i = 0; i < dimensions; i++) {

            average[i] =
                    average[i] / parsedEmbeddings.size();
        }


        return vectorToString(average);
    }


    private double[] parseVector(String vectorText) {

        String cleaned =
                vectorText
                        .replace("[", "")
                        .replace("]", "");


        String[] parts =
                cleaned.split(",");


        double[] vector =
                new double[parts.length];


        for (int i = 0; i < parts.length; i++) {

            vector[i] =
                    Double.parseDouble(
                            parts[i].trim()
                    );
        }


        return vector;
    }


    private String vectorToString(double[] vector) {

        StringBuilder builder =
                new StringBuilder("[");


        for (int i = 0; i < vector.length; i++) {

            builder.append(vector[i]);

            if (i < vector.length - 1) {
                builder.append(",");
            }
        }


        builder.append("]");


        return builder.toString();
    }
}