package com.example.ecommerce.vector;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Primary
public class OllamaEmbeddingGenerator
        implements EmbeddingGenerator {

    private final RestClient restClient;

    public OllamaEmbeddingGenerator() {

        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    @Override
    public String generate(String text) {

        Map<String, Object> requestBody = Map.of(
                "model", "nomic-embed-text",
                "input", text
        );

        OllamaEmbeddingResponse response =
                restClient.post()
                        .uri("/api/embed")
                        .body(requestBody)
                        .retrieve()
                        .body(OllamaEmbeddingResponse.class);

        if (response == null
                || response.embeddings() == null
                || response.embeddings().isEmpty()) {

            throw new IllegalStateException(
                    "Ollama returned no embedding"
            );
        }

        List<Double> embedding =
                response.embeddings().get(0);

        System.out.println(
                "Embedding dimensions: " + embedding.size()
        );

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

    public record OllamaEmbeddingResponse(
            List<List<Double>> embeddings
    ) {
    }
}