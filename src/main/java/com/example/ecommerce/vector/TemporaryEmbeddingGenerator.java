package com.example.ecommerce.vector;

import org.springframework.stereotype.Service;

@Service
public class TemporaryEmbeddingGenerator
        implements EmbeddingGenerator {

    @Override
    public String generate(String text) {

        String lowerText = text.toLowerCase();

        if (lowerText.contains("headphone")
                || lowerText.contains("earbud")
                || lowerText.contains("audio")
                || lowerText.contains("bluetooth")) {

            return "[0.90,0.80,0.10]";
        }

        if (lowerText.contains("chair")
                || lowerText.contains("office")) {

            return "[0.05,0.10,0.95]";
        }

        if (lowerText.contains("keyboard")
                || lowerText.contains("mouse")
                || lowerText.contains("gaming")) {

            return "[0.75,0.65,0.20]";
        }

        return "[0.30,0.30,0.30]";
    }
}