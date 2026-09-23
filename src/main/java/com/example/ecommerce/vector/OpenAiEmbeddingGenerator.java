package com.example.ecommerce.vector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@Service
//@Primary
public class OpenAiEmbeddingGenerator
        implements EmbeddingGenerator {

    private final RestClient restClient;
    private final String model;
    private final int dimensions;


    public OpenAiEmbeddingGenerator(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.embedding.model}") String model,
            @Value("${openai.embedding.dimensions}") int dimensions
    ) {

        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .build();

        this.model = model;
        this.dimensions = dimensions;
    }


    @Override
    public String generate(String text) {

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "input", text,
                "dimensions", dimensions
        );


        EmbeddingResponse response =
                restClient.post()
                        .uri("/embeddings")
                        .body(requestBody)
                        .retrieve()
                        .body(EmbeddingResponse.class);


        if (response == null
                || response.data() == null
                || response.data().isEmpty()) {

            throw new IllegalStateException(
                    "Embedding API returned no embedding"
            );
        }


        List<Double> embedding =
                response.data()
                        .get(0)
                        .embedding();


        return embedding.stream()
                .map(String::valueOf)
                .collect(
                        Collectors.joining(
                                ",",
                                "[",
                                "]"
                        )
                );
    }


    public record EmbeddingResponse(
            List<EmbeddingData> data
    ) {
    }


    public record EmbeddingData(
            List<Double> embedding
    ) {
    }
}